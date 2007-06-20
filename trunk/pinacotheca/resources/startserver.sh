#!/bin/sh

JAVA=$(which java)

PTINST="."
PTSERVERJAR=$PTINST"/pinacotheca.jar"
PTPROPERTIES=
PTSSLCERT=
PTSSLPASS=

$JAVA -Djavax.net.ssl.keyStore=$PTSSLCERT -Djavax.net.ssl.keyStorePassword=$PTSSLPASS -jar $PTSERVERJAR $PTPROPERTIES