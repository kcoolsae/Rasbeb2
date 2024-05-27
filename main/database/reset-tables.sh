#!/bin/bash
#
# reset-tables.sh
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
#
# This software is distributed under the MIT License - see files LICENSE and AUTHORS
# in the top level project directory.
#

#
# Reinitializes the database
export DATABASE
export DBUSER
export SERVER
./drop-tables.sh
./create-tables.sh
./import-tables.sh ${1}
