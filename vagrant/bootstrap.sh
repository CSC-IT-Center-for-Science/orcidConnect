#!/bin/bash

yum update -y
cd /etc/yum.repos.d/
curl -O http://download.opensuse.org/repositories/security://shibboleth/CentOS_7/security:shibboleth.repo
cd
yum -y install emacs-nox httpd shibboleth git maven java-1.8.0-openjdk-devel
yum -y remove java-1.7.0-openjdk java-1.7.0-openjdk-headless
systemctl enable shibd
systemctl enable httpd
systemctl start httpd
systemctl start shibd
pwd
ls
su vagrant -c "/home/vagrant/sync/gitstrap.sh"
