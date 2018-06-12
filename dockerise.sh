#!/usr/bin/env sh

docker build -t rkalis/blockchain-audit-trail . && \
docker run -it --rm -p 8080:8080 rkalis/blockchain-audit-trail
