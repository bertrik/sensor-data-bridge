FROM adoptopenjdk/openjdk14:jre-14.0.2_12-alpine
LABEL maintainer="Bertrik Sikken bertrik@gmail.com"

ADD sensor-data-bridge/build/distributions/sensor-data-bridge.tar /opt/

WORKDIR /opt/sensor-data-bridge
ENTRYPOINT ["/opt/sensor-data-bridge/bin/sensor-data-bridge"]

