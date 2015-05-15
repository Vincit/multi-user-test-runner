#!/bin/bash

VERSION_BUILD_NUMBER=".${GO_PIPELINE_COUNTER}"
echo "Building with build no. ${VERSION_BUILD_NUMBER}"

mvn clean test package -DbuildNumber="${VERSION_BUILD_NUMBER}"
