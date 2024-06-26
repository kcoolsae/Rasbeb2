CREATE TYPE event_status AS ENUM ('PENDING', 'OPEN', 'CLOSED');

CREATE TABLE events
(
    event_id     INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_status event_status NOT NULL DEFAULT 'PENDING',
    contest_id   INTEGER, -- references contests
    age_group_id INTEGER,
    school_id    INTEGER REFERENCES schools,
    year_id      INTEGER REFERENCES years,
    lang         TEXT,
    event_title  TEXT,

    when_created TIMESTAMP               DEFAULT NOW(),
    who_created  INTEGER,
    when_updated TIMESTAMP,
    who_updated  INTEGER,

    FOREIGN KEY (contest_id, age_group_id) REFERENCES contests_ag,
    FOREIGN KEY (contest_id, lang) REFERENCES contests_i18n
);

CREATE TRIGGER update_event
    BEFORE UPDATE
    ON events
    FOR EACH ROW
EXECUTE PROCEDURE update_when_modified();

--  events.sql
--  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--  Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
--
--  This software is distributed under the MIT License - see files LICENSE and AUTHORS
--  in the top level project directory.

-- if the record exists, then permission is granted to the given user to
-- participate in the local contest

CREATE TABLE permissions
(
    event_id     INTEGER REFERENCES events,
    pupil_id     INTEGER REFERENCES pupils,

    when_created TIMESTAMP DEFAULT NOW(),
    who_created  INTEGER,
    when_updated TIMESTAMP,
    who_updated  INTEGER,

    PRIMARY KEY (event_id, pupil_id)
);

CREATE TRIGGER update_permission
    BEFORE UPDATE
    ON permissions
    FOR EACH ROW
EXECUTE PROCEDURE update_when_modified();

