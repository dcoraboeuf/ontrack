#!/bin/bash

# Input data
ONTRACK=ontrack-test

# Set-up data
ONTRACK_DB=$ONTRACK
ONTRACK_APP=$ONTRACK

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

# Creating the application, deleting it if necessary
bees app:list | grep $ONTRACK_APP
if [ "$?" == "0" ]
then
	echo Deleting application $ONTRACK_APP...
	bees app:delete --force $ONTRACK_APP
	if [ "$?" != "0" ]
	then
		echo Could not delete the application.
		exit 1
	fi
fi
echo Creating application $ONTRACK_APP...
bees app:create --type tomcat $ONTRACK_APP
if [ "$?" != "0" ]
then
	echo Could not create the application.
	exit 1
fi
echo Application $ONTRACK_APP has been created.
