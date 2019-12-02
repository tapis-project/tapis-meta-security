#####################################################
#   Custom image build for Meta RestHeart Server
#   includes removing the Entrypoint and using the CMD for
#   java like the security service. Based on
#   Openjdk image.
#
# LABEL maintainer="TACC <sterry1@tacc.utexas.edu>"
# LABEL rh_core_ver=4.1.3
#
# Run from the Docker/restheart-server-build directory of the project
# Usage:
#   docker build --tag tapis/tapis-meta-rh-server:dev .
#
#   docker run tapis/tapis-meta-rh-server:dev --envFile etc/config.properties
#####################################################

FROM openjdk:11

LABEL maintainer="sterry1@tacc.utexas.edu"
LABEL rh-server-ver="4.1.5"

WORKDIR /opt/restheart
COPY etc etc/config.properties etc/
COPY restheart.jar /opt/restheart/

# ENTRYPOINT [ "java", "-Dfile.encoding=UTF-8", "-server", "-jar", "restheart.jar", "etc/restheart.yml"]
# CMD ["--envFile", "etc/config.properties"]
CMD ["bin/bash"]
EXPOSE 8080 4443
