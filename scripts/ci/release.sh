#!/bin/bash
set -e

./gradlew :core:publish :spring-test-class-runner:publish --stacktrace
