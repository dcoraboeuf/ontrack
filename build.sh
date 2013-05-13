#!/bin/bash

# How to perform a release?
#
# Set the following variables:
# MVN 			= path to Maven executable (can be as simple as "mvn" only)
# NEXUS_ID 		= ID for the Maven repository to deploy againt. Can be used to map with credentials in the Maven settings.xml file
# NEXUS_URL		= URL of the Maven repository to deploy to (see below)
#
# Then just execute the command:
# ./build.sh
#
# It will:
# - execute all the tests
# - package the application
# - export the application artifacts on the NEXUS_URL defined above
# - prepare all the POM files for the next release
#
# The release creates a commit for the preparation of the next release and this has to be pushed as well.
#
# The application artifacts are:
# - the WAR
# - the Jenkins plug-in (.hpi)

# How to use Git as Maven repository?
#
# In a directory $DIR, clone of the dcoraboeuf/mvnrepo repository
# git clone git@github.com:dcoraboeuf/mvnrepo.git $DIR
#
# Use this directory as target for the release:
# export NEXUS_ID=git-mvrepo
# export NEXUS_URL=file:/$DIR
#
# Perform the release operation as indicated above.
#
# You can then commit the artifacts and push as usual.

#############################
# Check environment variables
#############################

if [ "$MVN" == "" ]
then
	echo MVN is required.
	exit 1
fi

if [ "$NEXUS_URL" == "" ]
then
        echo NEXUS_URL is required.
        exit 1
fi

if [ "$NEXUS_ID" == "" ]
then
        echo NEXUS_ID is required.
        exit 1
fi

# Listing environment
echo "MVN          = ${MVN}"
echo "NEXUS_URL    = ${NEXUS_URL}"
echo "NEXUS_ID     = ${NEXUS_ID}"

##########################
# General MVN options
##########################

export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=128m -Djava.net.preferIPv4Stack=true"

##########################
# Preparation of the build
##########################

# Gets the version number from the POM
VERSION=`${MVN} help:evaluate -Dexpression=project.version $MVN_OPTIONS | grep -E "^[A-Za-z\.0-9]+-SNAPSHOT$" | sed -re 's/1\.([0-9]+)\-SNAPSHOT/\1/'`
echo Current version is $VERSION

# Gets the next version
let "NEXT_VERSION=$VERSION+1"
echo Next version is $NEXT_VERSION

# Release number is made of the version and the build number
RELEASE=1.${VERSION}
echo Building release ${RELEASE}...


#####################################################
# Runs the build itself and as much tests as possible
#####################################################

# Clean
git checkout -- .

# Changing the versions
${MVN} versions:set -DnewVersion=${RELEASE} -DgenerateBackupPoms=false

# Special case for Jenkins
sed -i "s/1.${VERSION}-SNAPSHOT/${RELEASE}/" ontrack-jenkins/pom.xml

# Maven build
${MVN} clean deploy -P it -P it-jetty -DaltDeploymentRepository=${NEXUS_ID}::default::${NEXUS_URL}
if [ $? -ne 0 ]
then
	echo Build failed.
	exit 1
fi

##################################################################
# After the build is complete, create the tag in Git and pushes it
##################################################################

echo Tagging...

# Tag
TAG=ontrack-${RELEASE}
# Tagging the build
git tag ${TAG}

#########################################
# Increment the version number and commit
#########################################

# Update the version locally
${MVN} versions:set -DnewVersion=1.${NEXT_VERSION}-SNAPSHOT -DgenerateBackupPoms=false

# Again, special case for Jenkins
sed -i "s/${RELEASE}<\/version>/1.${NEXT_VERSION}-SNAPSHOT<\/version>/" ontrack-jenkins/pom.xml

# Commits the update
git commit -am "Starting development of 1.${NEXT_VERSION}"


########################
# Clean-up & termination
########################

git checkout -- .

echo Done.
