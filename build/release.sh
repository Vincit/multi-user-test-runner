#!/bin/bash
set -e

if [ -z "$RELEASE_VERSION" ]; then
    echo "RELEASE_VERSION is missing"
    exit 1
fi

gradle uploadArchives
gradle tagRelease