#!/bin/sh -x

rm -rf /tmp/anode-release
git clone -b widget-no-logo ssh://lucretia/home/ssmith/programming/android/anode /tmp/anode-release
cd /tmp/anode-release

rm -rf src/image
android update project -p .

ant clean release
cp bin/Anode-unsigned.apk Anode-signed.apk

jarsigner -verbose -keystore /home/ssmith/programming/android/android-release-key.keystore \
   Anode-signed.apk release

zipalign -v 4 Anode-signed.apk Anode.apk


