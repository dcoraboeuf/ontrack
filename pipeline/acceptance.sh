#!/bin/bash

# Help function
function show_help {
	echo "Ontrack acceptance test script."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                    Displays this help"
	echo "Settings:"                        
	echo "    -m,--mvn=<path>               Path to the Maven executable ('mvn' by default)"
	echo "Test settings:"
	echo "    -u,--url=<url>                URL of the application to test"
	echo "    -v,--version=<version>        Used to check the version of the application being tested"
	echo "Ontrack on Ontrack:"
	echo "    --ontrack                     Notification of the build creation to an ontrack instance"
	echo "    --ontrack-branch              ontrack branch associated ('1.x' by default)"
	echo "    --ontrack-url                 ontrack URL ('http://ontrack.dcoraboeuf.cloudbees.net/' by default)"
	echo "    --ontrack-user                ontrack user"
	echo "    --ontrack-password            ontrack password"
	echo "    --ontrack-validation          ontrack associated validation stamp"
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
export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=128m -Djava.net.preferIPv4Stack=true"

# Defaults
MVN=mvn
ONTRACK=no
ONTRACK_BRANCH=1.x
ONTRACK_URL=http://ontrack.dcoraboeuf.cloudbees.net
ONTRACK_USER=
ONTRACK_PASSWORD=
ONTRACK_VALIDATION=
TEST_URL=
TEST_VERSION=

# Command central
for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-m=*|--mvn=*)
			MVN=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
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
		--ontrack-validation=*)
			ONTRACK_VALIDATION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-u=*|--url=*)
			TEST_URL=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-v=*|--version=*)
			TEST_VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checks
check "$MVN" "Maven executable (--mvn) is required."
check "$TEST_URL" "Test URL (--url) is required."
if [ "$ONTRACK" == "yes" ]
then
	check "$ONTRACK_USER" "ontrack user (--ontrack-user) is required."
	check "$ONTRACK_PASSWORD" "ontrack user (--ontrack-password) is required."
	check "$ONTRACK_VALIDATION" "ontrack validation stamp (--ontrack-validation) is required."
fi

# Values
echo Notifying ontrack:         ${ONTRACK}
if [ "$ONTRACK" == "yes" ]
then
	echo ontrack branch:            ${ONTRACK_BRANCH}
	echo ontrack URL:               ${ONTRACK_URL}
	echo ontrack validation stamp:  ${ONTRACK_VALIDATION}
fi
echo URL to test:               ${TEST_URL}
echo Version to test:           ${TEST_VERSION}

# Gets the version to test
VERSION=`curl --silent $TEST_URL/ui/manage/version`
if [ "$VERSION" == "" ]
then
	echo Could not get version information.
	exit 1
fi
echo Testing against version $VERSION
if [ "$TEST_VERSION" != "" -a "$TEST_VERSION" != "$VERSION" ]
then
	echo Expected version ${TEST_VERSION} but was ${VERSION}
	exit 1
fi

# Runs the acceptance tests
${MVN} clean verify -pl ontrack-acceptance -am -P it -DitUrl=${TEST_URL}
if [ "$?" != "0" ]
then
	echo Failed acceptance tests.
	STATUS=FAILED
else
	echo Passed acceptance tests.
	STATUS=PASSED
fi

# ontrack validation run
if [ "$ONTRACK" == "yes" ]
then
	echo Notifying the validation stamp ${ONTRACK_VALIDATION} at ${ONTRACK_URL}
	curl -i "${ONTRACK_URL}/ui/control/project/ontrack/branch/${ONTRACK_BRANCH}/build/ontrack-${VERSION}/validation_stamp/${ONTRACK_VALIDATION}" --user "${ONTRACK_USER}:${ONTRACK_PASSWORD}" --header "Content-Type: application/json" --data "{\"status\":\"${STATUS}\",\"description\":\"Run by acceptance.sh\"}"
fi
