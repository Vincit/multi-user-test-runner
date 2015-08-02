#!/bin/bash

SPRING_MAJOR_VERSION=$1
SPRING_VERSION="${SPRING_MAJOR_VERSION}"
SPRING_FOLDER="spring-test"

echo "Testing against ${SPRING_VERSION}"
echo " in folder ./${SPRING_FOLDER}"

mvn clean test -Dglobal.spring-version=${SPRING_VERSION} --projects core,spring-test
