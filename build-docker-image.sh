#!/bin/bash
set -e
# =================================================================
# Script to build the image for RestHeart security image deployment
# includes the nodeps jar from RestHeart security build
#   lib/restheart-security-1.2.4-SNAPSHOT-nodeps.jar
#
# target/tapis-meta-restheart-security.jar includes all the dependencies needed for standard
# RestHeart security instance.
#
# The image is created and published to the private registry for
# deployment.
# =================================================================

echo "###### Building Docker image..."
RHVERSION=1.2.4


echo "###### Building Docker image for RESTHeart Security Version $RHVERSION"
docker  build -t aloe/restheart:$RHVERSION .

docker tag aloe/restheart:$RHVERSION "jenkins2.tacc.utexas.edu:5000/tapis/restheart:$RHVERSION"
echo "###### Docker image successfully built."

docker push "jenkins2.tacc.utexas.edu:5000/aloe/restheart:$RHVERSION"