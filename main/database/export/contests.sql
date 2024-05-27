--  contests.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- age_groups not exported (immutable)
\copy contests(contest_id,contest_type,contest_status) TO 'contests.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy contests_i18n(contest_id,lang,contest_title) TO 'contests_i18n.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy contests_ag(contest_id,age_group_id,contest_duration) TO 'contests_ag.dat'  DELIMITER '|' ENCODING 'UTF-8'
