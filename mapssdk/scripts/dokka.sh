#!/bin/bash

### Runs the dokka documentation generator

cd `dirname $0` && cd ..

./gradlew dokka
while :; do
    # wait for modifications to the source directory (requires inotify-tools)
    inotifywait -r `find -type d -name src` -e modify 2> /dev/null
    sleep 1
    ./gradlew dokka
done


