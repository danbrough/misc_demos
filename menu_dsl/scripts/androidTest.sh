#!/bin/bash


cd `dirname $0` && cd ..

MODULE=app
PKG=danbroid.demo
TEST=danbroid.demo.LogTest
./gradlew :$MODULE:installDebug :$MODULE:installDebugAndroidTest || exit 1

adb shell am instrument -w -r    -e debug false -e class $TEST $PKG.test/androidx.test.runner.AndroidJUnitRunner





