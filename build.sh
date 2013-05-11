#!/bin/bash

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
${MVN} clean deploy -DaltDeploymentRepository=${NEXUS_ID}::default::${NEXUS_URL}
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
# Pushing the result
# git push origin ${TAG}

#########################################
# Increment the version number and commit
#########################################

# Update the version locally
${MVN} versions:set -DnewVersion=1.${NEXT_VERSION}-SNAPSHOT -DgenerateBackupPoms=false

# Again, special case for Jenkins
sed -i "s/${RELEASE}<\/version>/1.${NEXT_VERSION}-SNAPSHOT<\/version>/" ontrack-jenkins/pom.xml

# Commits the update
git commit -am "Starting development of 1.${NEXT_VERSION}"

# Pushing the result
# git push

########################
# Clean-up & termination
########################

git checkout -- .

echo Done.
