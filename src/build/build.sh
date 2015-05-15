#!/bin/bash

VERSION_BUILD_NUMBER=".${COUNT}"
echo "Building with build no. ${VERSION_BUILD_NUMBER}"

mvn clean test package -DbuildNumber="${VERSION_BUILD_NUMBER}"
