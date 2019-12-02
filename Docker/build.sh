#!/bin/bash
set -e

echo "###### start build.sh #####"

cd "$(dirname ${BASH_SOURCE[0]})"/..

mvn clean package
export VERSION=$(./bin/project-version.sh)
echo "###### Building Docker image for MetaV3 RH Security Version "$VERSION
docker build -t softinstigate/restheart-security .
docker tag tapis/tapis-meta-rh-security softinstigate/restheart-security:$VERSION
echo "###### end build.sh #####"
