#!/bin/bash

mvn clean test package -pl core,integration-test --projects core,integration-test,spring-test-class-runner
