#!/bin/sh


cd /var/git/fashionsmartcontract
nohup sh /var/git/fashionsmartcontract/start.sh > /var/log/fashion/smart.txt 2>&1 &

tail -f /var/log/fashion/smart.txt
