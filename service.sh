#!/bin/sh

dt=$(date '+%d%m%Y%H%M');

cd /var/log/fashion/
mv smart.txt smart"$dt".txt
gzip  smart"$dt".txt

cd /var/git/fashionsmartcontract
nohup sh /var/git/fashionsmartcontract/start.sh > /var/log/fashion/smart.txt 2>&1 &

tail -f /var/log/fashion/smart.txt
