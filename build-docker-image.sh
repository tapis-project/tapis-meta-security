#!/bin/bash
set -e
# =================================================================
# Script to build the image for RestHeart security image deployment
# includes the nodeps jar from RestHeart security build
#   lib/restheart-security-1.3.0-nodeps.jar
#
# target/tapis-meta-restheart-security.jar includes all the dependencies needed for standard
# RestHeart security instance.
#
# The image is created and published to the private registry for
# deployment.
# =================================================================

echo "###### Building jar artifacts ###### "
# basic build and install for plugin artifacts
mvn -f pom.xml clean install

echo "###### Building Docker image... ###### "
RHVERSION=dev


echo "  Building Docker image for RESTHeart Security Version $RHVERSION   "
docker  build -t tapis/tapis-meta-rh-security:$RHVERSION .

docker tag tapis/tapis-meta-rh-security:$RHVERSION "jenkins2.tacc.utexas.edu:5000/tapis/tapis-meta-rh-security:$RHVERSION"
echo "Docker image successfully built."

docker push "jenkins2.tacc.utexas.edu:5000/tapis/tapis-meta-rh-security:$RHVERSION"

docker push "tapis/tapis-meta-rh-security:$RHVERSION"