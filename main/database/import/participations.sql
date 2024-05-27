--  participations.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

\copy participations(contest_id,pupil_id,age_group_id,lang,event_id,participation_closed,participation_deadline,participation_total_marks) FROM 'participations.data' DELIMITER '|' ENCODING 'UTF-8'
\copy participation_details(contest_id,pupil_id,question_id,participation_answer,participation_marks) FROM 'participation_details.data' DELIMITER '|' ENCODING 'UTF-8'
