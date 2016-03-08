#!/bin/bash

yum update -y
cd /etc/yum.repos.d/
curl -O http://download.opensuse.org/repositories/security://shibboleth/CentOS_7/security:shibboleth.repo
cd
yum -y install emacs-nox java-1.7.0-openjdk java-1.8.0-openjdk java-1.8.0-openjdk-devel httpd shibboleth git maven mod_proxy_html policycoreutils-python mod_ssl wget
# epel repo and virtualbox guest additions
# vagrant guest additions plugin wasn't perfect
yum -y install epel-release
yum -y install dkms
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
# selinux permissive
# otherwise we would need to resolve discoveryFeed problem etc
cp /home/vagrant/sync/config /etc/selinux/


curl -O https://confluence.csc.fi/download/attachments/31195585/haka_testi_2015_sha2.crt
mv haka_testi_2015_sha2.crt /etc/pki/tls/certs/

cd /var/www/html
mkdir ds
cp /home/vagrant/sync/eds-conf.js ds/
curl -kO https://orcid-connect01.csc.fi/favicon.ico
mkdir img
cd img
wget --no-check-certificate -r --no-parent --reject "*.html" https://orcid-connect01.csc.fi/img/
mv orcid-connect01.csc.fi/img/*.png .
mv orcid-connect01.csc.fi/img/*.gif .
rm -rf orcid-connect01.csc.fi/

systemctl enable spring
systemctl start spring
systemctl start shibd
systemctl start httpd

# these shouldn't be necessary anymore now that we set selinux to permissive mode
#grep httpd_t /var/log/audit/audit.log | audit2allow -M shibd
#semodule -i shibd.pp