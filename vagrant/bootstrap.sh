#!/bin/bash

yum update
cd /etc/yum.repos.d/
curl -O http://download.opensuse.org/repositories/security://shibboleth/CentOS_7/security:shibboleth.repo
cd
yum -y install emacs-nox httpd shibboleth git maven java-1.8.0-openjdk-devel
systemctl enable shibd
systemctl enable httpd
systemctl start httpd
systemctl start shibd
su vagrant
cd
git clone https://klaalo@bitbucket.org/klaalo/orcidconnect.git
# export JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk
