\copy users(user_id,user_name,user_email,user_role,user_password_hash,user_password_salt,user_disabled) TO 'users.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy teachers(user_id,school_id) TO 'teachers.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy pupils(pupil_id,pupil_name,pupil_gender,pupil_password) TO 'pupils.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy pupils_classes(pupil_id,class_id) TO 'pupils_classes.dat' DELIMITER '|' ENCODING 'UTF-8'

--  people.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- NOTE registration and password_requests not exported