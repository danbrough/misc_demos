#!/bin/bash


cd `dirname $0` && cd ..
./gradlew refreshVersions

git diff versions.properties

