--  routines.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- Stored procedures stored by the system
--
-- TO maybe DO :-) rewrite them in Java

------------------
-- TRIGGERS
------------------

CREATE OR REPLACE FUNCTION update_when_modified()
	RETURNS TRIGGER AS $$
	BEGIN
	   NEW.when_updated = now();
	   RETURN NEW;
	END;
$$ LANGUAGE 'plpgsql';

-- Resets all the sequences of the given schema
-- 	SELECT reset_sequences('public');
--
-- Based on code from
--   https://github.com/josepmv/dbadailystuff/blob/master/postgresql_setval_max.sql
-- See also
--   https://wiki.postgresql.org/wiki/Fixing_Sequences

CREATE OR REPLACE FUNCTION reset_sequences(schema_name NAME)
    RETURNS VOID AS $$
DECLARE
    sql_code TEXT;
BEGIN
    FOR sql_code IN
	SELECT 'SELECT SETVAL(' ||
	     quote_literal(N.nspname || '.' || quote_ident(S.relname)) ||
	     ', MAX(' || quote_ident(C.attname)|| ') ) FROM ' ||
	     quote_ident(N.nspname) || '.' || quote_ident(T.relname)||
	     ';' AS sql_code
	FROM pg_class AS S
 	  INNER JOIN pg_depend AS D ON S.oid = D.objid
	  INNER JOIN pg_class AS T ON D.refobjid = T.oid
	  INNER JOIN pg_attribute AS C ON D.refobjid = C.attrelid AND D.refobjsubid = C.attnum
	  INNER JOIN pg_namespace N ON N.oid = S.relnamespace
		WHERE S.relkind = 'S' AND N.nspname = schema_name
		ORDER BY S.relname
        LOOP
            EXECUTE sql_code;
        END LOOP;
END; $$ LANGUAGE plpgsql;
