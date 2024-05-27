--  events.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

\copy events(event_id,event_status,contest_id,age_group_id,school_id,year_id,lang,event_title) TO 'events.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy permissions(event_id,pupil_id) TO 'permissions.dat' DELIMITER '|' ENCODING 'UTF-8'
