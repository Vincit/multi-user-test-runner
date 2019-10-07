#!/bin/bash

./gradlew clean build -x findbugsMain -x findbugsIntegrationTest -x findbugsTest -x integrationTest
