#! /bin/bash
sudo apt-get -y update

echo 'Install utils'
sudo apt install -y default-jdk
export JAVA_HOME=/usr/lib/jvm/default-java
sudo apt-get install -y nano
sudo apt-get install -y unrar
sudo apt-get install -y rar