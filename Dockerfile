FROM eclipse-temurin:17.0.10_7-jre-alpine

LABEL maintainer="Bertrik Sikken bertrik@gmail.com"

ADD sensor-data-bridge/build/distributions/sensor-data-bridge.tar /opt/

WORKDIR /opt/sensor-data-bridge
ENTRYPOINT ["/opt/sensor-data-bridge/bin/sensor-data-bridge"]

