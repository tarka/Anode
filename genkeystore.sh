#!/bin/bash

# Based on this: http://crazybob.org/2010/02/android-trusting-ssl-certificates.html

# Install the bouncy-castle jar with "sudo  apt-get install libbcprov-java"
BCJAR=/usr/share/java/bcprov.jar
export CLASSPATH=${BCJAR}:${CLASSPATH}

CERTSTORE=res/raw/geostore.bks
if [ -a $CERTSTORE ]; then
    rm $CERTSTORE
fi

wget http://www.geotrust.com/resources/root_certificates/certificates/GeoTrust_Global_CA.cer

keytool \
      -import \
      -v \
      -trustcacerts \
      -alias 0 \
      -file GeoTrust_Global_CA.cer \
      -keystore $CERTSTORE \
      -storetype BKS \
      -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
      -providerpath /usr/share/java/bcprov.jar \
      -storepass dummypass
