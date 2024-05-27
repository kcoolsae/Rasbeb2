#!/bin/bash
#
# import-tables.sh
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
#
# This software is distributed under the MIT License - see files LICENSE and AUTHORS
# in the top level project directory.
#

#
# Exports the contents of the database to a set of .dat files
DATABASE=${DATABASE:-rasbeb2}
DBUSER=${DBUSER:-rasbeb2}
SERVER=${SERVER:-localhost}
DATADIR=${1:-data}

if [ -d $DATADIR ]
then
   SCRIPT=$(pwd)/import/all.sql
   (cd $DATADIR; psql -h $SERVER $DATABASE $DBUSER -q -t -X -f $SCRIPT)
else
   echo "*** input directory $DATADIR does not exist ***"
fi
