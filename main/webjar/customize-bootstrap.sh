#!/bin/bash
#
# customize-bootstrap.sh
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
#
# This software is distributed under the MIT License - see files LICENSE and AUTHORS
# in the top level project directory.
#

#
# Customize bootstrap into rasbeb2-bootstrap.min.css
#

if [ scss/rasbeb2-bootstrap.scss -nt public/css/rasbeb2-bootstrap.min.css ]
then
  # only if source newer than result:
  if ! hash sass 2>/dev/null
  then
      echo >&2 "sass is required"
  elif ! [ -d bootstrap ]
  then
      echo >&2 "bootstrap source code is required"
  else
      sass scss/rasbeb2-bootstrap.scss public/css/rasbeb2-bootstrap.min.css --style=compressed
      cp bootstrap/node_modules/bootstrap/dist/js/bootstrap.bundle.min.js* public/js
  fi
fi
