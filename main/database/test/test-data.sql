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

-- School with id 1
INSERT INTO schools (school_id, school_name, school_street, school_zip, school_town)
VALUES (1, 'School, The', 'Schoolstreet 1', '1234', 'Rivendel');

-- User with id 2 belongs to school 1
INSERT INTO users (user_id, user_name, user_email, user_role)
VALUES (2, 'Teacher, The', 'teacher2@some.email.com', 'TEACHER');

INSERT INTO teachers (user_id, school_id)
VALUES (2, 1);

-- Request for registration that is expired
INSERT INTO registrations (user_email, school_id, registration_token, registration_expires)
VALUES ('newuser@some.email.com', 1, 'some token', now() - interval '4 days');

-- Request for password that is expired
INSERT INTO password_requests (user_email, request_token, request_expires)
VALUES ('teacher2@some.email.com', 'some token', now() - interval '2 hours');

-- Years
INSERT INTO years (year_id, year_name, when_created)
VALUES (23, '2023-2024', now() - interval '200 days'),
       (24, '2024-2025', now());




SELECT reset_sequences('public');

