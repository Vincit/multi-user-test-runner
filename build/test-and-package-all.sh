#!/bin/bash

mvn clean test package -Dglobal.version=${GO_PIPELINE_LABEL} --projects core,integration-test,java-8-test
