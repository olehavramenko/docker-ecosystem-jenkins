#!/bin/bash

wget https://nodejs.org/dist/v8.15.1/node-v8.15.1-linux-x64.tar.xz
apt-get install xz-utils
tar -xf node-v8.15.1-linux-x64.tar.xz  --directory=/usr/local --strip-components=1