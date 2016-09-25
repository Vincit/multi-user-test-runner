#!/bin/bash

gradle build -x findbugsMain -x findbugsIntegrationTest -x findbugsTest -x integrationTest -x signArchives
