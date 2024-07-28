--  test-data.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- Initialization script for the test database

-- Organiser with id 1
INSERT INTO users (user_id, user_name, user_email, user_role)
VALUES (1, 'Organiser, The', 'organiser@some.email.com', 'ORGANISER');

-- Years
INSERT INTO years (year_id, year_name, when_created)
VALUES (23, '2023-2024', now() - interval '200 days'),
       (24, '2024-2025', now()),
       (22, '2022-2023', now() - interval '400 days'); -- not referred to, can be deleted


-- Schools with id 1 and 2
INSERT INTO schools (school_id, school_name, school_street, school_zip, school_town)
VALUES
    (1, 'School, The', 'Schoolstreet 1', '1234', 'Rivendel'),
    (2, 'College, The', 'Street 22', '4321', 'Erehwon');

-- Classes
INSERT INTO classes (class_id, school_id, year_id, class_name)
VALUES
    (1, 1, 23, 'Old 3a'), (2, 1, 23, 'Old 3b'),
    (3, 1, 24, '3a'), (4, 1, 24, '3b'),
    (5, 2, 23, 'Old 2x'),
    (6, 2, 24, '2x'), (7, 2, 24, '2y'), (8, 2, 24, '2z');

-- Teachers
INSERT INTO users (user_id, user_name, user_email, user_role)
VALUES
    (2, 'Teacher, The', 'teacher2@some.email.com', 'TEACHER'),
    (3, 'Teacher 3, The', 'teacher3@some.email.com', 'TEACHER'),
    (4, 'Teacher 4, The', 'teacher4@some.email.com', 'TEACHER');

-- set passwords for all users to Opensesame
UPDATE users
SET user_password_hash = '399B572EB2D093D6833A07AD55C48CC0',
    user_password_salt = 'BA7D5D48D00ECB6E38D5DDED811B55D4'
WHERE user_id >= 1 AND user_id <= 4;

-- School 1 has two teachers (2 and 3), school 2 has one teacher (4)
INSERT INTO teachers (user_id, school_id)
VALUES (2, 1), (3, 1), (4, 2);

-- Request for registration that is expired
INSERT INTO registrations (user_email, school_id, registration_token, registration_expires)
VALUES ('newuser@some.email.com', 1, 'some token', now() - interval '4 days');

-- Request for password that is expired
INSERT INTO password_requests (user_email, request_token, request_expires)
VALUES ('teacher2@some.email.com', 'some token', now() - interval '2 hours');

-- Pupils
INSERT INTO pupils (pupil_id, pupil_name, pupil_gender, pupil_password)
VALUES
    (1, 'Pupil 1', 'MALE', 'password1'),
    (2, 'Pupil 2', 'FEMALE', 'password2'),
    (3, 'Pupil 3', 'OTHER', 'password3'),
    (4, 'Pupil 4', 'MALE', 'password4'),
    (5, 'Pupil 5', 'FEMALE', 'password5'),
    (6, 'Pupil 6', 'OTHER', 'password6');

-- Pupils in classes
INSERT INTO pupils_classes (pupil_id, class_id)
VALUES (1, 3), (2, 3), (5, 4), (6, 4), (3, 1), (4, 6);

-- Age groups (3)
INSERT INTO age_groups (age_group_id, lang, age_group_name, age_group_description)
SELECT id, language, 'Age group ' || id || ' in ' || language, 'Description of age group ' || id || ' in ' || language
FROM generate_series(1, 3) AS id
    CROSS JOIN unnest (ARRAY['en', 'nl', 'fr']) AS language;

-- Contests
INSERT INTO contests (contest_id, contest_type, contest_status)
VALUES (1, 'OFFICIAL', 'PUBLISHED'),
       (2, 'RESTRICTED', 'PENDING'),
       (3, 'RESTRICTED', 'OPEN'),
       (4, 'RESTRICTED', 'PUBLISHED'),
       (5, 'PUBLIC', 'OPEN'),
       (6, 'OFFICIAL', 'PENDING');  -- not referred to, can be deleted

-- Contest titles in various languages
INSERT INTO contests_i18n (contest_id, lang, contest_title)
SELECT contest_id, language, 'Contest ' || contest_id || ' in ' || language
FROM contests CROSS JOIN unnest (ARRAY['en', 'nl', 'fr']) AS language;

-- All have 3 languages, except contest 4
DELETE FROM contests_i18n WHERE contest_id = 4 AND lang = 'fr';

-- Supported age groups for contests
INSERT INTO contests_ag(contest_id, age_group_id, contest_duration)
VALUES (1, 1, 40), (1, 2, 40), (1, 3, 40),
       (2, 1, 40), (2, 2, 40),
       (3, 2, 40), (3, 3, 30),
       (4, 1, 40),
       (5, 1, 50), (5, 3, 30); -- no age groups for contest 6!

-- Questions
INSERT INTO questions(question_id, question_type, question_type_xtra, question_external_id, question_magic_q, question_magic_f)
VALUES
  (1, 'MC', '4', '2024-XY-01', 'MagicQ1', 'MagicF1'),
  (2, 'INT', NULL, '2024-XY-02', 'MagicQ2', 'MagicF2'),
  (3, 'TEXT', NULL, '2024-XY-12', 'MagicQ3', 'MagicF3');

-- Question titles in various languages
INSERT INTO questions_i18n (question_id, lang, question_title, question_correct_answer)
SELECT question_id, language,
       'Question ' || question_id || ' in ' || language,
       'Answer to ' || question_id || ' in ' || language
FROM questions CROSS JOIN unnest (ARRAY['fr', 'nl', 'en']) AS language;

-- Which questions in which contests:
-- In contest 1: age group 1 questions 1-3, ag 2 q 2-3, ag 3 q 3
--  i.e., q 1 ag 1, q 2 ag 1-2, q 3 ag 1-3
-- Marks increasingly (6,-2), (9,-3), (12, -4)
INSERT INTO questions_in_set (contest_id, age_group_id, question_id, question_marks_if_correct, question_marks_if_incorrect)
SELECT
    1,
    age_group_id,
    question_id,
    3* (question_id - age_group_id) + 6,
    age_group_id - question_id - 2
FROM age_groups JOIN questions
    ON question_id >= age_group_id AND lang = 'en'; -- age_groups contain each id 3 times

-- In contest 2-5 all questions for all supported age groups
INSERT INTO questions_in_set (contest_id, age_group_id, question_id, question_marks_if_correct, question_marks_if_incorrect)
SELECT
    contest_id,
    age_group_id,
    question_id,
    6,
    -2
FROM contests_ag CROSS JOIN questions WHERE contest_id > 1;

-- Events
INSERT INTO events (event_id, event_status,
                    contest_id, age_group_id, school_id, year_id,
                    lang, event_title)
VALUES
   (1, 'OPEN', 1, 2, 1, 24, 'nl', 'Event 1'),
   (2, 'PENDING', 1, 2, 1, 24, 'nl', 'Event 2'),
   (3, 'PENDING', 1, 3, 1, 24, 'fr', 'Event 3'),
   (4, 'CLOSED', 1, 3, 1, 24, 'en', 'Event 4'),
   (5, 'OPEN', 1, 3, 1, 23, 'en', 'Event 5'), -- wrong year
   (6, 'OPEN', 1, 3, 2, 24, 'en', 'Event 6'), -- wrong school
   (7, 'PENDING', 3, 2, 1, 24, 'en', 'Event 7'); -- other contest

-- Pupils registered for events
INSERT INTO permissions (event_id, pupil_id)
VALUES (1, 1), (1, 2),
       (2, 3), (2, 5),
       (3, 6),
       (7, 5), (7, 6);

SELECT reset_sequences('public');

