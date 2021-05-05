#!/bin/bash

rm -r build
mkdir -p build
cp ImfTest.aasx build/ImfTest.zip
pushd build
unzip ImfTest.zip
popd
cp build/aasx/**/*.xml out.xml
tidy -xml out.xml > /dev/null
