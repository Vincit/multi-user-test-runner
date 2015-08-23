#!/bin/bash

mvn clean test package --projects core,integration-test,java-8-test,spring-test-class-runner
