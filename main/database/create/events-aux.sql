--  events-aux.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- Auxiliary routines for events
-- NOTE Currently not used

-- delete permission from an entire class
--
-- parameters: event_id, class_id
CREATE OR REPLACE PROCEDURE revoke_permission_from_class(INTEGER, INTEGER) AS $$
BEGIN
    DELETE FROM permissions
    WHERE event_id = $1 AND pupil_id IN
          (SELECT pupil_id
           FROM classes
                    JOIN events USING (school_id)
                    JOIN pupils_classes USING (class_id)
           WHERE class_id = $2 AND event_id = $1);
END
$$ LANGUAGE 'plpgsql';
