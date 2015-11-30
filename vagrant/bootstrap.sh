#!/bin/bash

yum update -y
cd /etc/yum.repos.d/
curl -O http://download.opensuse.org/repositories/security://shibboleth/CentOS_7/security:shibboleth.repo
cd
yum -y install emacs-nox java-1.7.0-openjdk java-1.8.0-openjdk java-1.8.0-openjdk-devel httpd shibboleth git maven mod_proxy_html policycoreutils-python mod_ssl
systemctl enable shibd
systemctl enable httpd
su vagrant -c "/home/vagrant/sync/gitstrap.sh"
cp /home/vagrant/sync/spring.service /etc/systemd/system/
cp /home/vagrant/sync/spring.conf /etc/httpd/conf.d/
cp /home/vagrant/sync/orcid-saml.key /etc/pki/tls/private
chgrp shibd /etc/pki/tls/private/orcid-saml.key
cp /home/vagrant/sync/orcid-saml.pem /etc/pki/tls/certs

cp /home/vagrant/sync/shibboleth2.xml /etc/shibboleth/
cp /home/vagrant/sync/shib.conf /etc/httpd/conf.d/
cp /home/vagrant/sync/ssl.conf /etc/httpd/conf.d/

curl -O https://confluence.csc.fi/download/attachments/31195585/haka_testi_2015_sha2.crt
mv haka_testi_2015_sha2.crt /etc/pki/tls/certs/

systemctl enable spring
systemctl start spring
systemctl start shibd
systemctl start httpd

#grep httpd_t /var/log/audit/audit.log | audit2allow -M shibd
#semodule -i shibd.pp