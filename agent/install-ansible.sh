#!/bin/bash

apt update
apt install -y software-properties-common
apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 93C4A3FD7BB9C367

tee -a /etc/apt/sources.list.d/ansible-2.6.list << EOF
deb http://ppa.launchpad.net/ansible/ansible-2.6/ubuntu trusty main
deb-src http://ppa.launchpad.net/ansible/ansible-2.6/ubuntu trusty main
EOF

apt update
apt install -y ansible