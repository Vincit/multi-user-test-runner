#!/bin/bash

README_FILE="README.md"
RETURN_VALUE=0
RELEASE_VERSION=${cat version}

echo "Checking Maven example version. Expecting version $RELEASE_VERSION"
MAVEN_VERSION_COUNT=$(grep "<version>$RELEASE_VERSION</version>" $README_FILE | wc -l)

if [ $MAVEN_VERSION_COUNT -ne 1 ];
then
    echo " *** Wrong version for Maven example, found $MAVEN_VERSION_COUNT"
    RETURN_VALUE=1
fi

echo "Checking Gradle example version"
GRADLE_VERSION_COUNT_CORE=$(grep "fi.vincit:multi-user-test-runner:$RELEASE_VERSION" $README_FILE | wc -l)
GRADLE_VERSION_COUNT_SPRING=$(grep "fi.vincit:multi-user-test-runner-spring:$RELEASE_VERSION" $README_FILE | wc -l)

if [ $GRADLE_VERSION_COUNT_CORE -ne "1" ];
then
    echo " *** Wrong version for fi.vincit:multi-user-test-runner, found $GRADLE_VERSION_COUNT_CORE"
    RETURN_VALUE=1
fi

exit $RETURN_VALUE