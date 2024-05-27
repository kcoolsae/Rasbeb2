#!/bin/bash
#
# Create css files in public/css - only when source has changed
#

if [ scss/tables.scss -nt public/css/tables.css ]
then
  # only if source newer than result:
  if ! hash sass 2>/dev/null
  then
      echo >&2 "sass is required"
  else
      sass scss/tables.scss public/css/tables.css --no-source-map
  fi
fi
