#!/bin/bash

MAJOR=`echo $SPRING_VERSION | awk '{split($0,a,"."); print a[1]}'`
MINOR=`echo $SPRING_VERSION | awk '{split($0,a,"."); print a[2]}'`
PATCH=`echo $SPRING_VERSION | awk '{split($0,a,"."); print a[3]}'`

if [ $MAJOR -gt 4 ] || ( [ $MAJOR -eq 4 ] && [ $MINOR -ge 2 ] )
then
    gradle :spring-test:test
else
    echo "Skip tests for vesion ${SPRING_VERSION}"
fi
