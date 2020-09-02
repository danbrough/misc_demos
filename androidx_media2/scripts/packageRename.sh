#!/bin/bash


cd `dirname $0` && cd ..

PACKAGE=$1
OLD=`ls app/src/main/java/danbroid`
mv app/src/main/java/danbroid/$OLD app/src/main/java/danbroid/$PACKAGE

find app/src/ -type f  | grep -E 'xml|kt|java' | while read file; do
  sed -e 's:danbroid\.'$OLD':danbroid.'$PACKAGE':g' -i "$file";
done


