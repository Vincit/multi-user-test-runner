#!/bin/bash

./gradlew build check -x findbugsMain -x findbugsIntegrationTest -x findbugsTest -x integrationTest -x signArchives
