FROM maven:3.5.3-jdk-8 AS build

# Add pom.xml and install dependencies
COPY pom.xml /build/pom.xml
WORKDIR /build
RUN mvn clean

# Add source code and (by default) build the jar
COPY src /build/src
ARG BUILD_COMMAND="mvn package"
RUN ${BUILD_COMMAND}


FROM cidasdpdasartip.cr.usgs.gov:8447/wma/wma-spring-boot-base:latest

ENV HEALTHY_RESPONSE_CONTAINS='{"status":"UP"}'
COPY docker-entrypoint.sh /usr/local/bin/
COPY --from=build /build/target/nldi-services-*.jar app.jar
CMD ["docker-entrypoint.sh"]
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -k "http://127.0.0.1:8080/nldi/actuator/health" | grep -q ${HEALTHY_RESPONSE_CONTAINS} || exit 1
