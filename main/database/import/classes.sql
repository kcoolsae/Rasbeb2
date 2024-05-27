--  classes.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

\copy schools(school_id,school_name,school_street,school_zip,school_town) FROM 'schools.dat' DELIMITER '|' ENCODING 'UTF-8'
\copy years(year_id,year_name) FROM 'years.dat'  DELIMITER '|' ENCODING 'UTF-8'
\copy classes(class_id,school_id,year_id,class_name) FROM 'classes.dat' DELIMITER '|' ENCODING 'UTF-8'
