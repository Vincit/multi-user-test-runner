#!/bin/bash

SPRING_MAJOR_VERSION=$1
SPRING_VERSION="${SPRING_MAJOR_VERSION}"
SPRING_FOLDER="spring-test"

echo "Testing against: "
echo " Spring version: ${SPRING_VERSION}"
echo " in folder ./${SPRING_FOLDER}"

mvn clean test \
    -Dspring-core-version=${SPRING_VERSION} \
    --projects spring-test
