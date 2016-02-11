#! /bin/bash
rm -rf ~/Desktop/proj/data
mkdir ~/Desktop/proj/data
echo " export PGDATA=~/Desktop/proj/data " >> ~/.bash_profile
source ~/.bash_profile
initdb
echo " export PGPORT=2323" >> ~/.bash_profile
source ~/.bash_profile
pg_ctl -o -i -o "-p $PGPORT" -D $PGDATA -l logfile start
pg_ctl status
echo " export DB_NAME=TW_DB" >> ~/.bash_profile
source ~/.bash_profile
pg_ctl status
echo PGPORT: $PGPORT
echo PGDATA: $PGDATA
echo DB_NAME: $DB_NAME
echo USER: $USER
pg_ctl status
