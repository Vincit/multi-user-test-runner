#!/bin/bash

SPRING_MAJOR_VERSION=$1
SPRING_VERSION="${SPRING_MAJOR_VERSION}"
SPRING_FOLDER="spring-test"

SUT_VERSION=${GO_PIPELINE_LABEL}

echo "Testing against: "
echo " Spring version: ${SPRING_VERSION}"
echo " multi-user-test-runner: ${SUT_VERSION}"
echo " in folder ./${SPRING_FOLDER}"

mvn clean test \
    -Dglobal.spring-version=${SPRING_VERSION} \
    -Dglobal.version=${SUT_VERSION} \
    --projects spring-test
