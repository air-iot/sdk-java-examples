#!/bin/bash

# build docker image
docker build -t demo/mqtt-driver-demo:v4.0.0 .

# save docker image to tar file and compress
docker save demo/mqtt-driver-demo:v4.0.0 | gzip > mqtt-driver-demo.tar.gz

# copy or create service.yml
cp deployments/linux.yml service.yml

# create driver archive file
tar -czvf mqtt-driver-demo-linux-x86_64.tar.gz mqtt-driver-demo.tar.gz service.yml
