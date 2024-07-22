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

