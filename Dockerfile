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
#   docker run tapis/tapis-meta-rh-security:dev --envFile etc/dev.properties
#####################################################

FROM openjdk:11

LABEL maintainer="TACC <sterry1@tacc.utexas.edu>"
LABEL rh_security_ver=1.2.4
LABEL meta_rh_plugin_ver=1.0


WORKDIR /opt/restheart
COPY etc/*.yml /opt/restheart/etc/
COPY etc/*.properties /opt/restheart/etc/
COPY target/tapis-meta-restheart-security.jar /opt/restheart/
COPY lib/restheart-security-1.2.4-SNAPSHOT-nodeps.jar /opt/restheart/

# ENTRYPOINT [ "java", "-Dfile.encoding=UTF-8", "-server", "-cp", "restheart-security-1.2.4-SNAPSHOT-nodeps.jar:tapis-meta-restheart-security.jar", "org.restheart.security.Bootstrapper", "etc/restheart-security.yml"]
# CMD "java -Dfile.encoding=UTF-8 -server -cp restheart-security-1.2.4-SNAPSHOT-nodeps.jar:tapis-meta-restheart-security.jar org.restheart.security.Bootstrapper etc/restheart-security.yml --envFile etc/default.properties
CMD ["bin/bash"]

EXPOSE 8080
