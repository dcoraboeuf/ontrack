#!/bin/bash

# Main data
ONTRACK_DB=ontrack-test

# Creating the database, deleting it if necessary
bees db:list | grep $ONTRACK_DB
if [ "$?" == "0" ]
then
	echo Deleting database $ONTRACK_DB...
	bees db:delete --force $ONTRACK_DB
	if [ "$?" != "0" ]
	then
		echo Could not delete the database.
		exit 1
	fi
fi
echo Creating database $ONTRACK_DB...
bees db:create --username $ONTRACK_DB --password $ONTRACK_DB $ONTRACK_DB
if [ "$?" != "0" ]
	then
		echo Could not create the database.
		exit 1
	fi
echo Database $ONTRACK_DB has been created.
