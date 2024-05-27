#!/bin/bash
#
# drop-tables.sh
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
#
# This software is distributed under the MIT License - see files LICENSE and AUTHORS
# in the top level project directory.
#

#
# Drops all tables from the database
DATABASE=${DATABASE:-rasbeb2}
DBUSER=${DBUSER:-rasbeb2}
SERVER=${SERVER:-localhost}

psql -h $SERVER $DATABASE $DBUSER -q -t -X -f destroy/all.sql

