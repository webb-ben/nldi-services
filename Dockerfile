FROM maven:3.5.4-jdk-8 AS build

# Add pom.xml and install dependencies
COPY pom.xml /build/pom.xml
WORKDIR /build
RUN mvn clean

# Add source code and (by default) build the jar
COPY src /build/src
ARG BUILD_COMMAND="mvn package"
RUN ${BUILD_COMMAND}


FROM code.chs.usgs.gov:5001/wma-devops-public/wma-spring-boot-base:latest

ENV HEALTHY_RESPONSE_CONTAINS='{"status":"UP"}'
COPY --from=build /build/target/nldi-services-*.jar app.jar
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -k "https://127.0.0.1:${serverPort}${serverContextPath}${HEALTH_CHECK_ENDPOINT}" | grep -q ${HEALTHY_RESPONSE_CONTAINS} || exit 1
