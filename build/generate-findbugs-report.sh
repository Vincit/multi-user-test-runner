#!/bin/bash

mvn clean compile -Dglobal.version=${GO_PIPELINE_LABEL} -Pfindbugs
