#!/bin/bash -x

ver=$1
if [[ x$ver == "x" ]]; then
    echo Please supply a version to release
    exit
fi

rm -rf /tmp/anode-release
git clone git://github.com/tarka/Anode.git /tmp/anode-release
cd /tmp/anode-release
git co v$ver  -b release-$ver

rm -rf src/image
android update project -p .

ant clean release
cp bin/Anode-unsigned.apk Anode-signed.apk

jarsigner -verbose -keystore ~/programming/android/android-release-key.keystore \
   Anode-signed.apk release

zipalign -v 4 Anode-signed.apk Anode-${ver}.apk
