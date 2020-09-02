#!/bin/bash


cd `dirname $0` && cd ..

#arrIN=(${IN//;/ })



VERSION_NAME=$(./gradlew -q nextProjectVersion)

echo Creating release: $VERSION_NAME

while true; do
    read -p "Do you wish to proceed?: " yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

if git tag | grep "$VERSION_NAME" > /dev/null; then
  while true; do
    read -p "Existing Tag $VERSION_NAME found. Shall I delete it?: " yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
  done
  echo removing existing tag "$VERSION_NAME"
  git tag -d "$VERSION_NAME"
  git push origin --delete "$VERSION_NAME"
fi



incrementVersion(){
  VERSION=$(awk '/'$1'/ {print $5}' < buildSrc/src/main/kotlin/ProjectVersions.kt)
  VERSION=$((VERSION+1))
  KEY="$1"
  sed -i buildSrc/src/main/kotlin/ProjectVersions.kt  -e  's:'${KEY}' = .*:'${KEY}' = '$VERSION':g'
}

BETA_VERSION=$(awk '/BETA_VERSION/ {print $5}' < buildSrc/src/main/kotlin/ProjectVersions.kt)

incrementVersion "BUILD_VERSION"
if (( BETA_VERSION > -1 )); then
  incrementVersion "BETA_VERSION"
else
  incrementVersion "PROJECT_VERSION"
fi

#sed -i  README.md  -e 's/Latest version.*/Latest version: '$VERSION_NAME'/g'

git add .
git commit -am "$VERSION_NAME"
git tag "$VERSION_NAME" && git push && git push origin "$VERSION_NAME"




