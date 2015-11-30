#!/bin/bash

cd
git clone https://klaalo@bitbucket.org/klaalo/orcidconnect.git
export JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk
cp sync/private.properties orcidconnect/src/main/resources
cd orcidconnect
mvn package
