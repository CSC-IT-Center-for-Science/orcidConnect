# Orcid Connect Implementation

This is an implementation for Orcid Connect Service, which
will be used to connect Orcid identifier to local federated
identity. In Finland, this is eduPersonPrincipalName-
attribute that is commonly known identifier in Haka-
federation and other higher education user federations
across modern civilization.

## Building and usage

~~~~
git clone https://klaalo@bitbucket.org/klaalo/orcidconnect.git
# pom.xml is set to use Java 8, so in Centos7 install
# necessary packages and set correct JAVA_HOME
# In vagrantBox preliminaries should be handled
# for you so just setting JAVA_HOME will be sufficient.
# export JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk
#
# You will need private properties to filter variables.
# We'll assume that you have these on your vagrans sync
# folder.
# cp sync/private.properties orcidconnect/src/main/resources
#
cd orcidconnect
mvn spring-boot:run
~~~~  
