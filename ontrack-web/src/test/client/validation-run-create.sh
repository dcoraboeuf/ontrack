#!/bin/bash

PROJECT=$1
BRANCH=$2
BUILD=$3
VALIDATION_STAMP=$4
STATUS=$5
DESCRIPTION=$6

URL=http://localhost:8080/ontrack/ui/control/validation/${PROJECT}/${BRANCH}/${VALIDATION_STAMP}/${BUILD}
DATA="{\"status\":\"${STATUS}\",\"description\":\"${DESCRIPTION}\"}"

echo POST to $URL
echo DATA is
echo ${DATA}
echo
echo

curl ${URL} --header "Content-Type: application/json" --data "$DATA" $*

