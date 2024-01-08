#! /bin/bash
createdb -p $PGPORT $DB_NAME
pg_ctl status

