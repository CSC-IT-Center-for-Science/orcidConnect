# Orcid Connect Implementation

This is an implementation for Orcid Connect Service, which
will be used to connect Orcid identifier to local federated
identity. In Finland, this is eduPersonPrincipalName-
attribute which is commonly known identifier in Haka-
federation and other higher education user federations
across modern civilization.

OAuth2Client is derived from native implementation on Spring
framework because it lacks a support for multiple
authentication providers. On connect service we like to
have a possibility to link to both production API and to
sandbox for demonstration purposes.

As a side effect, this makes it possible to use this same
implementation to connect identities from other OAuth2
authentication providers as well.

## Standing on the shoulders of giants

[ *nanos gigantum humeris insidentes* ](https://en.wikipedia.org/wiki/Standing_on_the_shoulders_of_giants)

* [ Spring Framework ](https://spring.io)
* [ Spring Boot ](http://start.spring.io)
* [ Vagrant ](https://www.vagrantup.com)
* [ Ansible ](https://www.ansible.com)
* [ Maven ](http://maven.apache.org)

This list is not exhaustive. Please, see details from
the project description (*pom.xml*). 

## Requirements

* Java 1.8
* Vagrant > 1.9.1
* Ansible > 2.2.0.0

## Building and usage

~~~~
git clone https://github.com/CSC-IT-Center-for-Science/orcidConnect.git
# pom.xml is set to use Java 8, so in Centos7 install
# necessary packages and set correct JAVA_HOME
# In vagrantBox preliminaries should be handled
# for you so just setting JAVA_HOME will be sufficient.
# export JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk
#
# You will need private properties to filter variables.
# Ansible playbook will copy file for you.
cd orcidConnect/vagrant
vagrant up
vagrant ssh
mvn spring-boot:run
~~~~  

## Say again, what's that?

* [ Orcid ](http://orcid.org)
* [ Haka federation ](https://wiki.eduuni.fi/display/CSCHAKA/In+English)
* [ researcheridentifier.fi ](https://researcheridentifier.fi)