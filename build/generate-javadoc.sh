#!/bin/bash

cd core
mvn javadoc:javadoc -Dglobal.version=${GO_PIPELINE_LABEL}
cd ..
