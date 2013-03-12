#!/bin/bash

USER=$1
PASSWORD=$2
PROJECT=$3
BRANCH=$4
BUILD=$5
PROMOTION_LEVEL=$6
DESCRIPTION=$8

URL=http://localhost:8080/ontrack/ui/control/project/${PROJECT}/branch/${BRANCH}/build/${BUILD}/promotion_level/${PROMOTION_LEVEL}
DATA="{\"description\":\"${DESCRIPTION}\"}"

echo POST to $URL

curl "${URL}" --user ${USER}:${PASSWORD} --header "Content-Type: application/json" --data "$DATA"


