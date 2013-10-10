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
	echo "  -p, --profile=profile      Profile to use (default is 'prod' but could be 'it' for integration tests) -- used only at creation time"
	echo "Database:"
	echo "  -d, --database=dbname      ID of the database to create on Cloudbees (ontrack-test by default)."
	echo "  -cd, --create-database     Creates the database from scratch (not done by default)"
	echo "Version to deploy, either one of the following options:"
	echo "  -v, --version=version      Sets the ontrack version to deploy"
	echo "  -w, --war=warfile          Path to the WAR file to deploy"
	echo "Ontrack on Ontrack:"
	echo "    --ontrack                Notification of the deployment to an ontrack instance"
	echo "    --ontrack-branch         ontrack branch associated ('1.x' by default)"
	echo "    --ontrack-url            ontrack URL ('http://ontrack.dcoraboeuf.cloudbees.net/' by default)"
	echo "    --ontrack-user           ontrack user"
	echo "    --ontrack-password       ontrack password"
	echo "    --ontrack-promotion      ontrack promotion level to assign"
}

# Check function
function check {
	if [ "$1" == "" ]
	then
		echo $2
		exit 1
	fi
}

# General environment
ONTRACK_WD=target/deploy
ONTRACK_OWNER=dcoraboeuf

# Input data
ONTRACK_DB=ontrack-test
ONTRACK_APP=ontrack-test
ONTRACK_VERSION=
ONTRACK_WAR=
ONTRACK_DB_CREATE=no
ONTRACK_APP_CREATE=no
ONTRACK_APP_PROFILE=prod
ONTRACK=no
ONTRACK_BRANCH=1.x
ONTRACK_URL=http://ontrack.dcoraboeuf.cloudbees.net
ONTRACK_USER=
ONTRACK_PASSWORD=
ONTRACK_PROMOTION=

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
		-p=*|--profile=*)
			ONTRACK_APP_PROFILE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--ontrack)
			ONTRACK=yes
			;;
		--ontrack-branch=*)
			ONTRACK_BRANCH=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--ontrack-url=*)
			ONTRACK_URL=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--ontrack-user=*)
			ONTRACK_USER=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--ontrack-password=*)
			ONTRACK_PASSWORD=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--ontrack-promotion=*)
			ONTRACK_PROMOTION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
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
if [ "$ONTRACK" == "yes" ]
then
	check "$ONTRACK_VERSION" "ontrack version (--version) is required."
	check "$ONTRACK_USER" "ontrack user (--ontrack-user) is required."
	check "$ONTRACK_PASSWORD" "ontrack user (--ontrack-password) is required."
	check "$ONTRACK_PROMOTION" "ontrack promotion level (--ontrack-promotion) is required."
fi

# Echo
echo Ontrack CB application name      : $ONTRACK_APP
echo Ontrack CB database name         : $ONTRACK_DB
echo Ontrack CB database creation     : $ONTRACK_DB_CREATE
echo Ontrack CB application creation  : $ONTRACK_APP_CREATE
echo Ontrack CB application profile   : $ONTRACK_APP_PROFILE
if [ "$ONTRACK_VERSION" == "" ]
then
	echo Ontrack WAR to deploy            : $ONTRACK_WAR
else
	echo Ontrack version to deploy        : $ONTRACK_VERSION
fi
echo Notifying ontrack                : ${ONTRACK}
if [ "$ONTRACK" == "yes" ]
then
	echo ontrack branch                   : ${ONTRACK_BRANCH}
	echo ontrack URL                      : ${ONTRACK_URL}
	echo ontrack promotion level          : ${ONTRACK_PROMOTION}
fi

# General set-up
mkdir -p $ONTRACK_WD

# Getting the application
if [ "$ONTRACK_WAR" == "" ]
then
	rm -f $ONTRACK_WD/ontrack.war
	ONTRACK_DOWNLOAD_URL=https://github.com/${ONTRACK_OWNER}/ontrack/releases/download/ontrack-${ONTRACK_VERSION}/ontrack.war
	echo Downloading ontrack from $ONTRACK_DOWNLOAD_URL
	curl --location --silent --show-error --fail --output $ONTRACK_WD/ontrack.war $ONTRACK_DOWNLOAD_URL
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
	bees db:list | grep -e "$ONTRACK_DB$"
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
	bees app:list | grep -e "$ONTRACK_APP$"
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
	bees config:set --appid $ONTRACK_APP -R java_version=1.7 -P spring.profiles.active=$ONTRACK_APP_PROFILE -P ontrack.home=%{java.io.tmpdir}/ontrack/$ONTRACK_APP -P dbinit.profile=mysql
	echo Parameters have been set.

	echo Application $ONTRACK_APP has been created.

	# Binding the database

	bees app:bind --appid $ONTRACK_APP --database $ONTRACK_DB --alias ontrack maxActive=5 maxIdle=2 maxWait=30000 removeAbandoned=true removeAbandonedTimeout=60 logAbandoned=true validationQuery="SELECT 1" testOnBorrow=true defaultAutoCommit=false
fi

# Deploying the application
echo Starting deployment of $ONTRACK_WAR on $ONTRACK_APP...
bees app:deploy --appid $ONTRACK_APP --message "Deployment of version $ONTRACK_VERSION" $ONTRACK_WAR
echo Deployment finished.

# ontrack promoted run
if [ "$ONTRACK" == "yes" ]
then
	echo Notifying the promoted run ${ONTRACK_PROMOTION} at ${ONTRACK_URL}
	curl -i "${ONTRACK_URL}/ui/control/project/ontrack/branch/${ONTRACK_BRANCH}/build/ontrack-${VERSION}/promotion_level/${ONTRACK_PROMOTION}" --user "${ONTRACK_USER}:${ONTRACK_PASSWORD}" --header "Content-Type: application/json" --data "{\"description\":\"Run by cloudbees.sh\"}"
fi
