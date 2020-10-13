#!/bin/bash

set -e
set -x

cd github/minikube-build-tools-for-java

(cd minikube-gradle-plugin; ./gradlew --console=plain clean build)
(cd minikube-maven-plugin; ./mvnw -B -U clean verify)
