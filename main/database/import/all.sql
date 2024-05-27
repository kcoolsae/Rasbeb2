--  all.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- import data into an empty database
\ir classes.sql
\ir people.sql
\ir contests.sql
\ir questions.sql
\ir events.sql
\ir participations.sql

-- need to reset all sequences!
CALL reset_sequences('public');
