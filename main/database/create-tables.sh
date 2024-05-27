#!/bin/bash
#
# create-tables.sh
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
#
# This software is distributed under the MIT License - see files LICENSE and AUTHORS
# in the top level project directory.
#

#
# Creates all tables in an empty database. Use drop-tables if the database is not empty
DATABASE=${DATABASE:-rasbeb2}
DBUSER=${DBUSER:-rasbeb2}
SERVER=${SERVER:-localhost}

# check access
psql -U postgres </dev/null 2>/dev/null || \
    { echo "Adapt pg_hba.conf so it trusts postgres user"; exit 1; }

# check whether the new owner exists in the database. If not create it
hasowner=$(psql -U postgres -tAc \
    "SELECT 1 FROM pg_roles WHERE rolname='$DBUSER'")
if [ -z "$hasowner" ]
then
    # get owner password from .pgpass
    PASSWORD=$(grep -oP "(?<=:$DATABASE:$DBUSER:).*$" "$HOME"/.pgpass)
    [ -n "$PASSWORD" ] || { echo ".pgpass does not contain password for $DATABASE:$DBUSER"; exit 1; }
    psql -U postgres -tAc "CREATE USER $DBUSER WITH SUPERUSER ENCRYPTED PASSWORD '$PASSWORD'"
    echo "Created user $DBUSER as owner for database $DATABASE"
fi

# create database if it does not yet exist
hasdatabase=$(psql -U postgres -tAc \
     "SELECT 1 FROM pg_database where datname = '$DATABASE'")
if [ -z $hasdatabase ]
then
  psql -U postgres -tAc "CREATE DATABASE $DATABASE OWNER $DBUSER ENCODING 'UTF-8'"
fi

count=$(psql -h "$SERVER" "$DATABASE" "$DBUSER" -q -tAc \
   "select count(*) from information_schema.tables where table_schema='public'")
if [ "$count" != "0" ]
then
  echo "*** All tables must be dropped before using $0 ***"
  exit 1
else
  psql -h "$SERVER" "$DATABASE" "$DBUSER" -q -t -X -f create/all.sql
fi
