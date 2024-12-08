FROM eclipse-temurin:17.0.13_11-jre-alpine

LABEL maintainer="Bertrik Sikken bertrik@gmail.com"
LABEL org.opencontainers.image.source="https://github.com/bertrik/sensor-data-bridge"
LABEL org.opencontainers.image.description="Receives sensor data over TTN and forwards it to sensor.community"
LABEL org.opencontainers.image.licenses="MIT"

ADD sensor-data-bridge/build/distributions/sensor-data-bridge.tar /opt/

WORKDIR /opt/sensor-data-bridge
ENTRYPOINT ["/opt/sensor-data-bridge/bin/sensor-data-bridge"]

