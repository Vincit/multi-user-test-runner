#!/bin/bash

mvn clean test package -pl core,integration-test -Dglobal.version=${GO_PIPELINE_LABEL} --projects core,integration-test
