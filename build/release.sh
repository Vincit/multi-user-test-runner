#!/bin/bash

RELEASE_VERSION=${GO_PIPELINE_LABEL}
DEVEL_VERSION="0.2-SNAPSHOT"

mvn release:prepare-with-pom --projects .,core --batch-mode \
 -Dtag=${RELEASE_VERSION} \
 -DreleaseVersion=${RELEASE_VERSION} \
 -DdevelopmentVersion=${DEVEL_VERSION} \
 -e
