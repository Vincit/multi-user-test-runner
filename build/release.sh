#!/bin/bash

if [ -z "$RELEASE_VERSION" ]; then
    echo "RELEASE_VERSION is missing"
    exit 1
fi

if [ -z "$DEVELOPMENT_VERSION" ]; then
    echo "DEVELOPMENT_VERSION is missing"
    exit 1
fi

DEVEL_SNAPSHOT_VERSION="${DEVELOPMENT_VERSION}-SNAPSHOT"

mvn release:prepare --batch-mode \
 --projects core,spring-test-class-runner \
 -Dtag=${RELEASE_VERSION} \
 -DreleaseVersion=${RELEASE_VERSION} \
 -DdevelopmentVersion=${DEVEL_SNAPSHOT_VERSION} \
 -Dpassword=${SCM_PASSWORD} \
 -Dusername=${SCM_USERNAME} \
 -e

mvn release:perform --batch-mode \
 --projects core,spring-test-class-runner \
 -Dtag=${RELEASE_VERSION} \
 -DreleaseVersion=${RELEASE_VERSION} \
 -DdevelopmentVersion=${DEVEL_SNAPSHOT_VERSION} \
 -Dpassword=${SCM_PASSWORD} \
 -Dusername=${SCM_USERNAME} \
 -e
