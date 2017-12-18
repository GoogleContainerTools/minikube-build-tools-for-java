#!/bin/bash

set -e
set -x

cd github/minikube-build-tools-for-java

sudo /opt/google-cloud-sdk/bin/gcloud components install docker-credential-gcr

(cd minikube-gradle-plugin; ./gradlew clean build)
(cd minikube-maven-plugin; ./mvnw clean install)
(cd crepecake; ./gradlew clean build integrationTest --info)
