#!/bin/bash

mvn clean test -Dglobal.version=${GO_PIPELINE_LABEL}
