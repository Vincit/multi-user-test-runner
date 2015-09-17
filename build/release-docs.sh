#!/bin/bash

echo "Releasing documentation"

VERSION=$RELEASE_VERSION
DOCS_FULL_URL="https://${DOCS_USERNAME}:${DOCS_PASSWORD}@${DOCS_GIT_REPO}"
DOCS_BRANCH=gh-pages
DOCS_DIR=docs
PROJECT_DIR=../project

echo " * Generate JavaDoc for version $RELEASE_VERSION"
cd project
./build/generate-javadoc.sh
cd ..

echo " * Cloning docs from $DOCS_GIT_REPO branch $DOCS_BRANCH to $DOCS_DIR"
git clone $DOCS_FULL_URL -b $DOCS_BRANCH --single-branch $DOCS_DIR
cd $DOCS_DIR

# Copy JavaDocs
JAVA_VERSION_DOC_DIR="$VERSION/javadocs"
JAVA_LATEST_DOC_DIR="latest/javadocs"

echo " * Copying JavaDocs to $JAVA_VERSION_DOC_DIR"
mkdir -p $JAVA_VERSION_DOC_DIR
cp -r $PROJECT_DIR/core/build/docs/javadoc/** $JAVA_VERSION_DOC_DIR

echo " * Copying JavaDocs to $JAVA_LATEST_DOC_DIR"
mkdir -p $JAVA_LATEST_DOC_DIR
rm -rf $JAVA_LATEST_DOC_DIR/*
cp -r $PROJECT_DIR/core/build/docs/javadoc/** $JAVA_LATEST_DOC_DIR

# Copy README.md
README_FILE="README.md"
CHANGELOG_FILE="CHANGELOG.md"
LATEST_DIR="_includes/latest"
VERSION_DIR="_includes/$VERSION"

echo " * Copying $README_FILE to $LATEST_DIR"
mkdir -p LATEST_DIR
cp $PROJECT_DIR/$README_FILE $LATEST_DIR/$README_FILE
cp $PROJECT_DIR/$CHANGELOG_FILE $LATEST_DIR/$CHANGELOG_FILE

echo " * Copying $README_FILE to $VERSION_DIR"
mkdir -p $VERSION_DIR
cp $PROJECT_DIR/$README_FILE $VERSION_DIR/$README_FILE

# Push to site
echo " * Pusing to site"
git add $JAVA_VERSION_DOC_DIR/**
git add $JAVA_LATEST_DOC_DIR/**
git add $VERSION_DIR/$README_FILE
git add $LATEST_DIR/$README_FILE
git add $LATEST_DIR/$CHANGELOG_FILE

git commit -m "Release $VERSION docs"

git push origin HEAD:gh-pages
