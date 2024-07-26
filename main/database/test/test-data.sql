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
       (5, 'PUBLIC', 'OPEN');

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
       (5, 1, 50), (5, 3, 30);

SELECT reset_sequences('public');

