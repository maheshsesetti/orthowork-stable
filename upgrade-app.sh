#!/bin/bash
set -ex

git submodule foreach git pull origin main

# start hsdlc common
pushd common
jhipster upgrade
popd

# start api
pushd api
jhipster upgrade
popd

# start notification app
pushd notification
jhipster upgrade
popd

# start invoice app
pushd invoice
nohup ./mvnw &  > ../logs/invoice.log 2>&1
jhipster upgrade
popd

# start gateway
pushd orthoworks
jhipster upgrade
popd
