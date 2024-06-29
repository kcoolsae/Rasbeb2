-- questions
------------

CREATE TYPE answer_type AS ENUM ('MC', 'INT', 'TEXT', 'JSON', 'CWC');

-- multiple choice, open integer, open text, javascript provided json,
-- correct when completed - cannot be answered incorrectly, task must simply be completed
-- to be considered correct

-- contains all language independent information for a question
CREATE TABLE questions
(
    question_id          INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 2000) PRIMARY KEY,
    question_type        answer_type NOT NULL,
    question_type_xtra   TEXT,                 -- for MC: number of answer options, for INTERACTIVE, JAVASCRIPT: ??
    question_external_id TEXT UNIQUE NOT NULL, -- e.g, 2024-BE-01

    -- magic numbers used for storing the question on an external site
    question_magic_q     TEXT        NOT NULL, -- for question text
    question_magic_f     TEXT        NOT NULL, -- for feedback text

    when_created         TIMESTAMP DEFAULT NOW(),
    who_created          INTEGER,
    when_updated         TIMESTAMP,
    who_updated          INTEGER

);

CREATE OR REPLACE FUNCTION question_magic_numbers()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.question_magic_q IS NULL THEN
        NEW.question_magic_q = upper(
                    lpad(to_hex(2 * NEW.question_id), 8, '0') ||
                    substr(md5(random()::text), 10)
            );
    END IF;
    IF NEW.question_magic_f IS NULL THEN
        NEW.question_magic_f = upper(
                    lpad(to_hex(2 * NEW.question_id + 1), 8, '0') ||
                    substr(md5(random()::text), 10)
            );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER update_question
    BEFORE UPDATE
    ON questions
    FOR EACH ROW
EXECUTE PROCEDURE update_when_modified();

CREATE TRIGGER create_question
    BEFORE INSERT
    ON questions
    FOR EACH ROW
EXECUTE PROCEDURE question_magic_numbers();

-- contains language dependent information
CREATE TABLE questions_i18n
(
    question_id             INTEGER REFERENCES questions,
    lang                    CHAR(2),

    question_title          TEXT DEFAULT '',
    question_correct_answer TEXT DEFAULT '',

    question_uploaded_q     BOOLEAN   DEFAULT FALSE, -- was a question text already uploaded?
    question_uploaded_f     BOOLEAN   DEFAULT FALSE, -- was a feedback text already uploaded?

    when_created            TIMESTAMP DEFAULT NOW(),
    who_created             INTEGER,
    when_updated            TIMESTAMP,
    who_updated             INTEGER,

    PRIMARY KEY (question_id, lang)
);

CREATE TRIGGER update_question_i18n
    BEFORE UPDATE
    ON questions_i18n
    FOR EACH ROW
EXECUTE PROCEDURE update_when_modified();

-- question sets
----------------

-- defines which questions go into a contest for a certain age group
CREATE TABLE questions_in_set
(
    contest_id                  INTEGER, -- references contests
    age_group_id                INTEGER,
    question_id                 INTEGER REFERENCES questions,

    question_seq_nr             INTEGER GENERATED BY DEFAULT AS IDENTITY, -- used to fix the ordering within the set
    question_marks_if_correct   INTEGER,
    question_marks_if_incorrect INTEGER,

    when_created                TIMESTAMP DEFAULT NOW(),
    who_created                 INTEGER,
    when_updated                TIMESTAMP,
    who_updated                 INTEGER,

    PRIMARY KEY (contest_id, age_group_id, question_id),
    FOREIGN KEY (contest_id, age_group_id) REFERENCES contests_ag

);

CREATE TRIGGER update_question_in_set
    BEFORE UPDATE
    ON questions_in_set
    FOR EACH ROW
EXECUTE PROCEDURE update_when_modified();

-- contains suggestions for marks corresponding to difficulty levels easy: 1, medium: 2, difficult: 3
-- This should be considered a read only table
CREATE TABLE marks
(
    marks_level               INTEGER PRIMARY KEY, -- 1, 2 or 3
    question_marks_if_correct INTEGER,
    question_marks_if_wrong   INTEGER
);

--  questions.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- Auxiliary function for use in creating a filtered/sorted table of questions with all
-- selections made for contest with contest_id=c

CREATE OR REPLACE FUNCTION questionsWithAgeGroups(c INTEGER)
    RETURNS TABLE(
        question_id INTEGER,
        question_external_id TEXT,
        question_title TEXT,
        lang CHAR(2),
        age_group_list INTEGER[]
    ) AS $$
BEGIN
    RETURN QUERY
      SELECT q.question_id, q.question_external_id, i.question_title, i.lang, array_agg(s.age_group_id)
        FROM questions AS q
        JOIN questions_i18n AS i USING (question_id)
        LEFT JOIN questions_in_set AS s ON (
            q.question_id = s.question_id and contest_id = c
        )
        GROUP BY contest_id, q.question_id, q.question_external_id, i.question_title, i.lang ;
END;
$$ LANGUAGE 'plpgsql';

-- Sets/resets age groups that use a particular question in a particular contest
-- Keeps sequence number untouched

CREATE OR REPLACE PROCEDURE setQuestionAgeGroups (
        c_id Integer, q_id Integer, age_groups Integer[], who INTEGER
        ) AS $$
DECLARE
    a_g INTEGER;
BEGIN
    FOR a_g IN
        SELECT age_group_id FROM age_groups
    LOOP
        IF array_position(age_groups,a_g) IS NOT NULL
        THEN
            INSERT INTO questions_in_set(question_id, contest_id, age_group_id, who_created)
            VALUES (q_id,c_id,a_g,who)
            ON CONFLICT DO NOTHING;
        ELSE
            DELETE FROM questions_in_set
            WHERE question_id = q_id AND contest_id = c_id AND age_group_id = a_g;
        END IF;
    END LOOP;
END;
$$ LANGUAGE 'plpgsql';