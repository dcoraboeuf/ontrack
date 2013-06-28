#!/bin/bash

# Help function
function show_help {
        echo "Ontrack Cloudbees deployment script."
		echo ""
		echo "Available options are:"
		echo "General:"
		echo "  -h, --help                 Displays this help"
		echo "Application:"
		echo "  -a, --appid=appid          ID of the application to create on Cloudbees (ontrack-test by default)"
		echo "  -ca, --create-application  Creates the application from scratch (not done by default)"
		echo "Database:"
		echo "  -d, --database=dbname      ID of the database to create on Cloudbees (ontrack-test by default)."
		echo "  -cd, --create-database     Creates the database from scratch (not done by default)"
		echo "Version to deploy, either one of the following options:"
		echo "  -v, --version=version      Sets the ontrack version to deploy"
		echo "  -w, --war=warfile          Path to the WAR file to deploy"
}

# General environment
ONTRACK_REPO=https://repository-dcoraboeuf.forge.cloudbees.com/release/net/ontrack/ontrack-web
ONTRACK_WD=target/deploy

# Input data
ONTRACK_DB=ontrack-test
ONTRACK_APP=ontrack-test
ONTRACK_VERSION=
ONTRACK_WAR=
ONTRACK_DB_CREATE=no
ONTRACK_APP_CREATE=no

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
		-w=*|--war=*)
			ONTRACK_WAR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-a=*|--appid=*)
			ONTRACK_APP=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-d=*|--database=*)
			ONTRACK_DB=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-d=*|--database=*)
			ONTRACK_DB=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-cd|--create-database)
			ONTRACK_DB_CREATE=yes
			;;
		-ca|--create-application)
			ONTRACK_APP_CREATE=yes
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checks
if [ "$ONTRACK_VERSION" == "" -a "$ONTRACK_WAR" == "" ]
then
	echo "Ontrack version (--version) or WAR file (--war) is required."
	show_help
	exit 1
fi

# Echo
echo Ontrack CB application name      : $ONTRACK_APP
echo Ontrack CB database name         : $ONTRACK_DB
echo Ontrack CB database creation     : $ONTRACK_DB_CREATE
echo Ontrack CB application  creation : $ONTRACK_APP_CREATE
if [ "$ONTRACK_VERSION" == "" ]
then
	echo Ontrack WAR to deploy            : $ONTRACK_WAR
else
	echo Ontrack version to deploy        : $ONTRACK_VERSION
fi

# General set-up
mkdir -p $ONTRACK_WD

# Getting the application
if [ "$ONTRACK_WAR" == "" ]
then
	rm -f $ONTRACK_WD/ontrack.war
	ONTRACK_URL=$ONTRACK_REPO/$ONTRACK_VERSION/ontrack-web-$ONTRACK_VERSION.war
	echo Downloading ontrack from $ONTRACK_URL
	curl --show-error --fail --output $ONTRACK_WD/ontrack.war $ONTRACK_URL
	if [ "$?" != "0" ]
	then
		echo Error while downloading ontrack $ONTRACK_VERSION from $ONTRACK_REPO
		exit 1
	fi
	ONTRACK_WAR=$ONTRACK_WD/ontrack.war
fi

# Creating the database, deleting it if necessary
if [ "$ONTRACK_DB_CREATE" == "yes" ]
then
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
fi

# Creating the application, deleting it if necessary
if [ "$ONTRACK_APP_CREATE" == "yes" ]
then
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
	echo Setting the production profile...
	echo Setting the home directory...
	echo Setting the DB profile to mysql...
	bees config:set --appid $ONTRACK_APP -R java_version=1.7 -P spring.profiles.active=prod -P ontrack.home=/private/ontrack/$ONTRACK_APP -P dbinit.profile=mysql
	echo Parameters have been set.

	echo Application $ONTRACK_APP has been created.

	# Binding the database

	bees app:bind --appid $ONTRACK_APP --database $ONTRACK_DB --alias ontrack maxActive=5 maxIdle=2 maxWait=30000 removeAbandoned=true removeAbandonedTimeout=60 logAbandoned=true validationQuery="SELECT 1" testOnBorrow=true defaultAutoCommit=false
fi

# Deploying the application
echo Starting deployment of $ONTRACK_WAR on $ONTRACK_APP...
bees app:deploy --appid $ONTRACK_APP --message "Deployment of version $ONTRACK_VERSION" $ONTRACK_WAR
echo Deployment finished.
