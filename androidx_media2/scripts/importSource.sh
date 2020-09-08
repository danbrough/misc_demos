#!/bin/bash

cd `dirname $0` && cd ..
rm -rf stuff/player 2> /dev/null
cp -av /mnt/files/dan/workspace/android/support_src/media2/player/src/main/java/androidx/media2/player/  stuff/
cd stuff/player
sed -e 's:androidx.media2.player:androidx.media2.customplayer:g' -i *
sed -e 's:androidx.media2.exoplayer.external:com.google.android.exoplayer2:g' -i *

