#!/bin/bash

TARGET_DIR="test-results-all/"

mkdir -p "${TARGET_DIR}"

cp core/build/test-results/test "${TARGET_DIR}" || :
cp examples/build/test-results/test "${TARGET_DIR}" || :
cp examples/build/test-results/integrationTest "${TARGET_DIR}" || :
cp integration-test/build/test-results/test "${TARGET_DIR}" || :
cp junit-5-legacy-test/build/test-results/test "${TARGET_DIR}" || :
cp junit-5-test/build/test-results/test "${TARGET_DIR}" || :
cp spring-test/build/test-results/test "${TARGET_DIR}" || :
