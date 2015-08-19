#!/bin/bash

RELEASE_VERSION=${GO_PIPELINE_LABEL}
DEVEL_VERSION="${NEXT_VERSION}-SNAPSHOT"

mvn release:prepare --batch-mode \
 --projects core \
 -Dtag=${RELEASE_VERSION} \
 -DreleaseVersion=${RELEASE_VERSION} \
 -DdevelopmentVersion=${DEVEL_VERSION} \
 -Dpassword=${SCM_PASSWORD} \
 -Dusername=${SCM_USERNAME} \
 -e

mvn release:perform --batch-mode \
 --projects core \
 -Dtag=${RELEASE_VERSION} \
 -DreleaseVersion=${RELEASE_VERSION} \
 -DdevelopmentVersion=${DEVEL_VERSION} \
 -Dpassword=${SCM_PASSWORD} \
 -Dusername=${SCM_USERNAME} \
 -e
