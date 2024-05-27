--  contests.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

\copy age_groups(age_group_id,lang,age_group_name,age_group_description) FROM 'age_groups.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy contests(contest_id,contest_type,contest_status) FROM 'contests.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy contests_i18n(contest_id,lang,contest_title) FROM 'contests_i18n.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy contests_ag(contest_id,age_group_id,contest_duration) FROM 'contests_ag.dat'  DELIMITER '|' ENCODING 'UTF-8'
