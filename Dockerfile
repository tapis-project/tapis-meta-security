#
# Usage:
# docker build --tag tapis-meta-rh-security:1.2.4 .
#
FROM gcr.io/distroless/java:11

LABEL maintainer="TACC <sterry1@tacc.utexas.edu>"

WORKDIR /opt/restheart
COPY etc/*.yml /opt/restheart/etc/
COPY etc/*.properties /opt/restheart/etc/
COPY target/tapis-meta-restheart-security.jar /opt/restheart/
COPY lib/restheart-security-1.2.4-SNAPSHOT-nodeps.jar /opt/restheart/

ENTRYPOINT [ "java", "-Dfile.encoding=UTF-8", "-server", "-cp", "restheart-security-1.2.4-SNAPSHOT-nodeps.jar:tapis-meta-restheart-security.jar", "org.restheart.security.Bootstrapper", "etc/restheart-security.yml"]
CMD ["--envFile", "etc/default.properties"]
EXPOSE 8080
