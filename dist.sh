#! /usr/bin/env bash

DERBY_VERSION=10.9.1.0
DERBY_JAR=derby-$DERBY_VERSION.jar
M2_HOME=$HOME/.m2
M2_REPO=$M2_HOME/repository
DIST_DIR=dist
LIB_DIR=$DIST_DIR/lib
START_SCRIPT=$DIST_DIR/jweatherhistory.sh

mkdir -p $LIB_DIR;

for i in $(find . -iname jweather*.jar); do cp $i $LIB_DIR/; done;
for i in $(find $M2_REPO/com/jgoodies -iname *.jar); do cp $i $LIB_DIR/; done;
for i in $(find $M2_REPO/org/apache -iname *$DERBY_JAR); do cp $i $LIB_DIR/; done;

echo "#! /usr/bin/env bash
\
java -cp \"lib/*\" za.co.johanmynhardt.jweatherhistory.gui.JWeatherHistoryUI\
" | tee $START_SCRIPT

chmod a+x $START_SCRIPT
