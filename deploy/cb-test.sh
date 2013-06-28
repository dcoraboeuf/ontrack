#!/bin/bash

# Help function
function show_help {
        echo "Ontrack Cloudbees deployment script."
		echo ""
		echo "Available options are:"
		echo "  -h, --help              Displays this help"
		echo "  -v, --version=version   Sets the ontrack version to deploy (required)"
		echo "  -a, --appid=appid       ID of the application to create on Cloudbees (ontrack-test by default)"
		echo "  -d, --database=dbname   ID of the database to create on Cloudbees (ontrack-test by default)."
}

# Input data
ONTRACK_DB=ontrack-test
ONTRACK_APP=ontrack-test
ONTRACK_VERSION=

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-v=*|--version=*)
			ONTRACK_VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-a=*|--appid=*)
			ONTRACK_APP=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-d=*|--database=*)
			ONTRACK_DB=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
				# unknown option
		;;
	esac
done

# Checks
if [ "$ONTRACK_VERSION" == "" ]
then
	echo Ontrack version -v is required.
	show_help
	exit 1
fi

# Echo
echo Ontrack version to deploy   : $ONTRACK_VERSION
echo Ontrack CB application name : $ONTRACK_APP
echo Ontrack CB database name    : $ONTRACK_DB

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

echo Setting the JDK version to 1.7...
bees config:set --appid $ONTRACK_APP -R java_version=1.7

echo Setting the production profile...
bees config:set --appid $ONTRACK_APP -P spring.profiles.active=prod

echo Setting the home directory
bees config:set --appid $ONTRACK_APP -P ontrack.home=/private/ontrack/$ONTRACK_APP

echo Application $ONTRACK_APP has been created.

# Binding the database

bees app:bind --appid $ONTRACK_APP --database $ONTRACK_DB --alias ontrack


