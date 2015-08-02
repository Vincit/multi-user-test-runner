#!/bin/bash

mvn clean install -Dglobal.version=${GO_PIPELINE_LABEL} --projects core
