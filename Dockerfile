# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine
MAINTAINER Bertrik Sikken bertrik@gmail.com

ADD sensor-data-bridge/build/distributions/sensor-data-bridge.tar /opt/

WORKDIR /opt/sensor-data-bridge
ENTRYPOINT /opt/sensor-data-bridge/bin/sensor-data-bridge

