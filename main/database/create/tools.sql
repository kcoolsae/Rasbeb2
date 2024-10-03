--  tools.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- Additional stored procedures for more complicated tasks

-- Copy contest as a new restricted contest with the same questions
-- used at the end of the year to enable teachers to use these in their classes
-- Returns the id of the new contest

CREATE OR REPLACE FUNCTION copy_contest(
    old_id INTEGER,  -- id of the contest to copy
    who INTEGER
) RETURNS INTEGER AS $$
DECLARE
    new_id INTEGER; -- id of the new contest
BEGIN
    -- new contest
    INSERT INTO contests (contest_type, contest_status, who_created)
        VALUES ('RESTRICTED'::contest_type, 'OPEN'::contest_status, who)
        RETURNING contest_id INTO new_id;
    -- copy titles
    INSERT INTO contests_i18n (contest_id, lang, contest_title, who_created)
        SELECT new_id, lang, contest_title || ' (+)', who
        FROM contests_i18n WHERE contest_id = old_id;
    -- copy age groups
    INSERT INTO contests_ag (contest_id, age_group_id, contest_duration, who_created)
        SELECT new_id, age_group_id, contest_duration, who
        FROM contests_ag WHERE contest_id = old_id;
    -- copy questions
    INSERT INTO questions_in_set (contest_id, age_group_id, question_id, question_seq_nr,
                                  question_marks_if_correct, question_marks_if_incorrect, who_created)
        SELECT new_id, age_group_id, question_id, question_seq_nr,
                question_marks_if_correct, question_marks_if_incorrect, who
        FROM questions_in_set WHERE contest_id = old_id;

    RETURN new_id;

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION winners(
    c_id INTEGER,  -- id of the contest
    a_id INTEGER,  -- id of the age group
    n INTEGER  -- number of winners
) RETURNS TABLE (pupil_name TEXT, school_name TEXT, school_town TEXT, marks INTEGER) AS $$
DECLARE
    rank INTEGER;
BEGIN
    SELECT participation_total_marks
    FROM participations
    WHERE contest_id = c_id AND age_group_id = a_id AND NOT participation_hidden
    ORDER BY participation_total_marks DESC OFFSET n-1 LIMIT 1 INTO rank;
    RETURN QUERY
        SELECT pupils.pupil_name, schools.school_name, schools.school_town, participation_total_marks
        FROM participations
                 JOIN pupils USING (pupil_id)
                 JOIN events USING (event_id,contest_id,age_group_id)
                 JOIN schools USING (school_id)
        WHERE participation_total_marks >= rank AND
            contest_id = c_id AND
            age_group_id = a_id AND
            NOT participation_hidden
        ORDER BY participation_total_marks DESC, schools.school_name;
END;
$$ LANGUAGE plpgsql;
