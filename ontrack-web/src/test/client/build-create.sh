#!/bin/bash

USER=$1
PASSWORD=$2
PROJECT=$3
BRANCH=$4
NAME=$5
DESCRIPTION=$6

URL=http://localhost:8080/ontrack/ui/control/build/${PROJECT}/${BRANCH}
DATA="{\"name\":\"${NAME}\",\"description\":\"${DESCRIPTION}\"}"

echo POST to $URL

curl "${URL}" --user ${USER}:${PASSWORD} --header "Content-Type: application/json" --data "$DATA" 

