#!/usr/bin/env bash

VERSION=0.92.0.0
URL='http://towny.palmergames.com/file-repo/Towny%20Advanced/Releases/0.92.0.0%20%281.111.12%29/Towny_Advanced.zip'

mkdir /tmp/towny
cd /tmp/towny

echo Please download ${URL} and drop it into `pwd`\!
echo To confirm press enter ...
read

unzip Towny_Advanced.zip

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=Towny.jar \
                                                                     -DgroupId=com.palmergames.towny \
                                                                     -DartifactId=towny \
                                                                     -Dversion=${VERSION} \
                                                                     -Dpackaging=jar

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=TownyChat.jar \
                                                                     -DgroupId=com.palmergames.towny \
                                                                     -DartifactId=chat \
                                                                     -Dversion=${VERSION} \
                                                                     -Dpackaging=jar

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=TownyNameUpdater.jar \
                                                                     -DgroupId=com.palmergames.towny \
                                                                     -DartifactId=name-updater \
                                                                     -Dversion=${VERSION} \
                                                                     -Dpackaging=jar

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=Questioner.jar \
                                                                     -DgroupId=com.palmergames.towny \
                                                                     -DartifactId=questioner \
                                                                     -Dversion=${VERSION} \
                                                                     -Dpackaging=jar

cd /tmp
rm -r towny/
