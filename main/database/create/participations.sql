--  participations.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- Participation of a pupil in a contest
CREATE TABLE participations
(
    contest_id   INTEGER, -- references contests
    pupil_id     INTEGER REFERENCES pupils,
    age_group_id INTEGER,
    lang         CHAR(2),
    event_id     INTEGER REFERENCES events, -- null when anonymous participation

    participation_closed BOOLEAN DEFAULT FALSE,
    participation_deadline TIMESTAMP,

    participation_total_marks INTEGER DEFAULT 0, -- filled in after closed

    when_created TIMESTAMP DEFAULT NOW(),
    who_created  INTEGER,
    when_updated TIMESTAMP,
    who_updated  INTEGER,

    PRIMARY KEY (contest_id, pupil_id),
    FOREIGN KEY (contest_id, age_group_id) REFERENCES contests_ag,
    FOREIGN KEY (contest_id, lang) REFERENCES contests_i18n
);

CREATE TRIGGER update_participation
    BEFORE UPDATE
    ON participations
    FOR EACH ROW
EXECUTE PROCEDURE update_when_modified();

CREATE TABLE participation_details
(
    contest_id   INTEGER, -- references contests
    pupil_id     INTEGER, -- references pupils
    question_id  INTEGER REFERENCES questions,

    participation_answer TEXT,
    participation_marks INTEGER DEFAULT 0, -- filled in after participation closes

    when_created TIMESTAMP DEFAULT NOW(),
    who_created  INTEGER,
    when_updated TIMESTAMP,
    who_updated  INTEGER,

    PRIMARY KEY (contest_id, pupil_id, question_id),
    FOREIGN KEY (contest_id, pupil_id) REFERENCES participations
);

CREATE TRIGGER update_participation_detail
    BEFORE UPDATE OF participation_answer -- not triggered when the marks are computed
    ON participation_details
    FOR EACH ROW
EXECUTE PROCEDURE update_when_modified();

-- Auxiliary views and routines
-------------------------------

-- List of questions with correct answers and marks
CREATE VIEW questions_with_answer AS
    SELECT contest_id, age_group_id, question_id, lang,
           question_correct_answer, question_marks_if_correct, question_marks_if_incorrect
    FROM questions_in_set JOIN questions_i18n USING (question_id);

-- Sum of marks over an entire question set
CREATE MATERIALIZED VIEW question_sets AS
    SELECT contest_id, age_group_id,
           SUM(question_marks_if_correct) as max_marks,
           SUM(question_marks_if_incorrect) as min_marks
    FROM questions_in_set
    GROUP BY contest_id, age_group_id;

-- Update the marks in participation_details for a certain contest and pupil
-- Auxiliary procedure, use close_participations or close_event instead
CREATE OR REPLACE PROCEDURE compute_individual_marks (c_id INT, p_id INT) AS $$
BEGIN
    WITH info AS (
        SELECT question_id, question_correct_answer, question_marks_if_correct, question_marks_if_incorrect
          FROM questions_with_answer
              JOIN participations USING (contest_id,age_group_id, lang)
          WHERE contest_id = c_id AND pupil_id = p_id )
    UPDATE participation_details AS pd
       SET participation_marks =
           CASE
                WHEN (participation_answer = '') IS NOT FALSE THEN 0 -- includes null case
                WHEN participation_answer = info.question_correct_answer THEN
                             info.question_marks_if_correct
                ELSE
                             info.question_marks_if_incorrect
            END
    FROM info
    WHERE pd.question_id = info.question_id AND contest_id = c_id AND pupil_id = p_id;
END; $$ LANGUAGE plpgsql;

-- Close a participation and compute the total marks
-- To be used for public (anonymous) contests, use close_event in other cases
CREATE OR REPLACE PROCEDURE close_participation (c_id INT, p_id INT, who_upd INT) AS $$
DECLARE
    diff INT;
BEGIN
    CALL compute_individual_marks(c_id, p_id);

    SELECT min_marks INTO diff
    FROM question_sets
        JOIN participations USING (contest_id,age_group_id)
    WHERE contest_id = c_id AND pupil_id = p_id;

    WITH marks AS (
        SELECT COALESCE(SUM(participation_marks),0) AS total
        FROM participation_details
        WHERE contest_id = c_id AND pupil_id = p_id
    )
    UPDATE participations
    SET participation_closed = true,
        participation_total_marks = marks.total - diff,
        who_updated = who_upd
    FROM marks
    WHERE contest_id = c_id AND pupil_id = p_id;
END; $$ LANGUAGE plpgsql;

-- Close an event, close the related participations and compute the related marks
-- Used for events related to restricted contests, use close_contest for public
CREATE OR REPLACE PROCEDURE close_event (e_id INT, who_upd INT)  AS $$
DECLARE
    c_id INT;
    p_id INT;
BEGIN
    FOR c_id, p_id IN
      SELECT contest_id, pupil_id FROM participations
      WHERE participations.event_id = e_id
    LOOP
        CALL close_participation(c_id, p_id, who_upd);
    END LOOP;

    UPDATE events
    SET event_status = 'CLOSED'::event_status, who_updated = who_upd
    WHERE event_id = e_id;
END; $$ LANGUAGE plpgsql;

-- Close a (public) contest, close the related participations, compute the related marks
-- and close the related events

CREATE OR REPLACE PROCEDURE close_contest (c_id INT, who_upd INT)  AS $$
DECLARE
    p_id INT;
BEGIN
    FOR p_id IN
        SELECT pupil_id FROM participations
        WHERE participations.contest_id = c_id
    LOOP
        CALL close_participation(c_id, p_id, who_upd);
    END LOOP;

    UPDATE events
    SET event_status = 'CLOSED'::event_status, who_updated = who_upd
    WHERE contest_id = c_id;

    UPDATE contests
    SET contest_status = 'CLOSED'::contest_status, who_updated = who_upd
    WHERE contest_id = c_id AND contest_type = 'PUBLIC'::contest_status;
END; $$ LANGUAGE plpgsql;

