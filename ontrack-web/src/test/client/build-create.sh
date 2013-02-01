#!/bin/bash

PROJECT=$1
BRANCH=$2
NAME=$3
DESCRIPTION=$4

URL=http://localhost:8080/ontrack/ui/control/build/${PROJECT}/${BRANCH}
DATA="{\"name\":\"${NAME}\",\"description\":\"${DESCRIPTION}\"}"

echo POST to $URL

curl http://localhost:8080/ontrack/ui/control/build/EBANK/2.6 --header "Content-Type: application/json" --data $DATA