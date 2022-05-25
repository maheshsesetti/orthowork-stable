#!/bin/bash
set -ex

mkdir -p logs

# start service registry
pushd jhipster-registry
nohup ./mvnw &  > ../logs/jhipster-registry.log 2>&1
popd

# start hsdlc common
pushd common
nohup ./mvnw & > ../logs/common.log 2>&1
popd

# start api
pushd api
nohup ./mvnw & > ../logs/api.log 2>&1
popd

# start notification app
pushd notification
nohup ./mvnw &  > ../logs/notification.log 2>&1
popd

# start invoice app
pushd invoice
nohup ./mvnw &  > ../logs/invoice.log 2>&1
popd

# start gateway
pushd orthoworks

	# Remove this one faulty generated file that's apparently of no use to the application anyway
	rm -f src/test/java/com/orthoworks/store/cucumber/stepdefs/UserStepDefs.java
nohup ./mvnw  & > ../logs/orthoworks-mvn.log 2>&1
popd
