#!/bin/bash
#
# init-test-data.sh
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
#
# This software is distributed under the MIT License - see files LICENSE and AUTHORS
# in the top level project directory.
#

#
# Initializes the test database with data which is expected to be there by the unit test
DATABASE=${DATABASE:-rasbeb2test}
DBUSER=${DBUSER:-rasbeb2}
SERVER=${SERVER:-localhost}

SCRIPT=test/test-data.sql

hasdata=$(psql -h $SERVER $DATABASE $DBUSER -tAc "SELECT 1 FROM users")
if [ -n "$hasdata" ]
then
  echo "*** Test data already present in $DATABASE ***"
  exit 1
else
  psql -h $SERVER $DATABASE $DBUSER -q -t -X -f $SCRIPT
  exit 0
fi
