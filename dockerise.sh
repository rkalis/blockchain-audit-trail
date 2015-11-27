#!/usr/bin/env sh

mvn clean install && docker build -t rkalis/contactapp . && docker run -it --rm -p 8888:8080 rkalis/contactapp #-p "isis.manifist=1231231;isis.database.driver=sqlServer;"
