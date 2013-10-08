#!/bin/bash

# Help function
function show_help {
	echo "Ontrack build script."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                    Displays this help"
	echo "Settings:"                        
	echo "    -m,--mvn=<path>               Path to the Maven executable ('mvn' by default)"
	echo "    --push                        Pushes to the remote Git"
	echo "    --deploy                      Uploads the artifacts to the repository"
	echo "    --github-user=<user>          User for GitHub (default to 'dcoraboeuf')"
	echo "    --github-token=<token>        API token for GitHub"
	echo "Release numbering:"                         
	echo "    -v,--version=<release>        Version to prepare (by default extracted from the POM, by deleting the -SNAPSHOT prefix)"
	echo "    -nv,--next-version=<release>  Next version to prepare (by default, the prepared version where the last digit is incremented by 1)"
	echo "Ontrack on Ontrack:"
	echo "    --ontrack                     Notification of the build creation to an ontrack instance"
	echo "    --ontrack-branch              ontrack branch associated ('1.x' by default)"
	echo "    --ontrack-url                 ontrack URL ('http://ontrack.dcoraboeuf.cloudbees.net/' by default)"
	echo "    --ontrack-user                ontrack user"
	echo "    --ontrack-password            ontrack password"
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
export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true"

# Defaults
MVN=mvn
VERSION=
NEXT_VERSION=
GIT_PUSH=no
DEPLOY=no
ONTRACK=no
ONTRACK_BRANCH=1.x
ONTRACK_URL=http://ontrack.dcoraboeuf.cloudbees.net
ONTRACK_USER=
ONTRACK_PASSWORD=
GITHUB_USER=dcoraboeuf
GITHUB_TOKEN=

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
		-v=*|--version=*)
			VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-nv=*|--next-version=*)
			NEXT_VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--push)
			GIT_PUSH=yes
			;;
		--deploy)
			DEPLOY=yes
			;;
		--github-user=*)
			GITHUB_USER=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--github-token=*)
			GITHUB_TOKEN=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
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
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checks
check "$MVN" "Maven executable (--mvn) is required."
if [ "$ONTRACK" == "yes" ]
then
	check "$ONTRACK_USER" "ontrack user (--ontrack-user) is required."
	check "$ONTRACK_PASSWORD" "ontrack user (--ontrack-password) is required."
fi
if [ "$DEPLOY" == "yes" ]
then
	check "$GITHUB_USER" "GitHub user (--github-user) is required."
	check "$GITHUB_TOKEN" "GitHub API token (--github-token) is required."
	# Forces push to yes
	GIT_PUSH = "yes"
fi

# Preparation of the version

CURRENT_VERSION=`${MVN} help:evaluate -Dexpression=project.version $MVN_OPTIONS | grep -E "^[A-Za-z\.0-9]+-SNAPSHOT$" | sed -re 's/([A-Za-z\.0-9]+)\-SNAPSHOT/\1/'`

if [ "$VERSION" == "" ]
then
	# Gets the version number from the POM
	VERSION=${CURRENT_VERSION}
fi

# Preparation of the next version
if [ "$NEXT_VERSION" == "" ]
then
	VERSION_LAST_DIGIT=`echo $VERSION | sed -re 's/.*\.([0-9]+)$/\1/'`
	VERSION_PREFIX=`echo $VERSION | sed -re 's/(.*)\.[0-9]+$/\1/'`
	let "NEXT_VERSION_NUMBER=$VERSION_LAST_DIGIT+1"
	NEXT_VERSION="$VERSION_PREFIX.$NEXT_VERSION_NUMBER"
fi

# All variables
echo Current version:           ${CURRENT_VERSION}
echo Version to build:          ${VERSION}
echo Next version to promote:   ${NEXT_VERSION}
echo Pushing to Git:            ${GIT_PUSH}
echo Notifying ontrack:         ${ONTRACK}
if [ "$ONTRACK" == "yes" ]
then
	echo ontrack branch:            ${ONTRACK_BRANCH}
	echo ontrack URL:               ${ONTRACK_URL}
fi
echo Deploying the artifacts:   ${DEPLOY}
if [ "$DEPLOY" == "yes" ]
then
	echo GitHub user:               ${GITHUB_USER}
fi
	
# Cleaning the environment
echo Cleaning the environment...
git checkout -- .

# Adding the version number in a property file
echo version=${VERSION} > version.properties

# Updating the versions
echo Updating versions to ${VERSION}
${MVN} --quiet versions:set -DnewVersion=${VERSION} -DgenerateBackupPoms=false

# Special case for Jenkins
sed -i "s/${CURRENT_VERSION}-SNAPSHOT/${VERSION}/" ontrack-jenkins/pom.xml

# Maven build
echo Launching build...
${MVN} clean install -P it -P it-jetty -DaltDeploymentRepository=${NEXUS_ID}::default::${NEXUS_URL}
if [ $? -ne 0 ]
then
	echo Build failed.
	exit 1
fi
	
# After the build is complete, create the tag
TAG=ontrack-${VERSION}
echo Tagging to $TAG
git tag ${TAG}

# Increment the version number and commit
echo Changing to the next version: ${NEXT_VERSION}-SNAPSHOT

# Update the version locally
${MVN} --quiet versions:set -DnewVersion=${NEXT_VERSION}-SNAPSHOT -DgenerateBackupPoms=false

# Again, special case for Jenkins
sed -i "s/${VERSION}<\/version>/${NEXT_VERSION}-SNAPSHOT<\/version>/" ontrack-jenkins/pom.xml

# Commits the update
echo Committing the next version changes
git commit -am "Starting development of ${NEXT_VERSION}"

# Pushing
if [ "$GIT_PUSH" == "yes" ]
then
	echo Pushing to the remote repository
	git push --verbose
	echo Pushing the tags to the remote repository
	git push --tags --verbose

	# Deployment of artifacts
	if [ "$DEPLOY" == "yes" ]
	then
		# Creating the release on GitHub
		echo Creating the release on GitHub
		curl -i --header "Accept: application/vnd.github.manifold-preview" --user "${GITHUB_USER}:${GITHUB_TOKEN}"  https://api.github.com/repos/${GITHUB_USER}/ontrack/releases --data "{\"tag_name\":\"${TAG}\"}"
	fi
	
fi

# ontrack
if [ "$ONTRACK" == "yes" ]
then
	echo Notifying the build creation at ${ONTRACK_URL}
	curl -i "${ONTRACK_URL}/ui/control/project/ontrack/branch/${ONTRACK_BRANCH}/build" --user "${ONTRACK_USER}:${ONTRACK_PASSWORD}" --header "Content-Type: application/json" --data "{\"name\":\"ontrack-${VERSION}\",\"description\":\"Created by build.sh\"}"
fi

# End
echo Build done.
