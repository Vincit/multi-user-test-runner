#!/bin/bash

mvn clean test package -Dglobal.version=${GO_PIPELINE_LABEL}
