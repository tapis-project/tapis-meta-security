#####################################################
#   Custom image build for Meta RestHeart Security
#   includes the custom Tapis IDM plugin. Based on
#   Openjdk image.
#
# LABEL maintainer="TACC <sterry1@tacc.utexas.edu>"
# LABEL rh_security_ver=1.2.4
# LABEL meta_rh_plugin_ver=1.0
#
# Run from the root of the project
# Usage:
#   docker build --tag tapis/tapis-meta-rh-security:dev .
#
#   docker run tapis/tapis-meta-rh-security:dev --envFile etc/default-security.properties
#
#   java -Dfile.encoding=UTF-8 -server -cp restheart-security-1.3.0-nodeps.jar:tapis-meta-restheart-security.jar org.restheart.security.Bootstrapper etc/restheart-security.yml --envFile etc/default-security.properties
#
#####################################################

FROM openjdk:11.0.5-jdk-stretch

LABEL maintainer="TACC <sterry1@tacc.utexas.edu>"
LABEL rh_security_ver=1.3.0
LABEL meta_rh_plugin_ver=1.0


WORKDIR /opt/restheart
RUN apt update; apt install nano

COPY Docker/etc/restheart-security.yml /opt/restheart/etc/
COPY Docker/etc/default-security.properties /opt/restheart/etc/
COPY Docker/etc/acl.yml   /opt/restheart/etc/
COPY Docker/etc/users.yml   /opt/restheart/etc/
# put jar artifacts in image
COPY target/tapis-meta-restheart-security.jar /opt/restheart/
COPY lib/restheart-security-1.3.0-nodeps.jar /opt/restheart/

# ENTRYPOINT [ "java", "-Dfile.encoding=UTF-8", "-server", "-cp", "restheart-security-1.2.4-SNAPSHOT-nodeps.jar:tapis-meta-restheart-security.jar", "org.restheart.security.Bootstrapper", "etc/restheart-security.yml"]
# CMD "java -Dfile.encoding=UTF-8 -server -cp restheart-security-1.3.0-nodeps.jar:tapis-meta-restheart-security.jar org.restheart.security.Bootstrapper etc/restheart-security.yml --envFile etc/default-security.properties
CMD ["bin/bash"]

EXPOSE 8080
