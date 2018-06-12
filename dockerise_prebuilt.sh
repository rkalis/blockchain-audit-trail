#!/usr/bin/env sh

mvn clean install -DskipTests && \
docker build -t rkalis/blockchain-audit-trail -d Dockerfile_prebuilt . && \
docker run -it --rm -p 8080:8080 rkalis/blockchain-audit-trail
