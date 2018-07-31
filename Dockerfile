FROM code.chs.usgs.gov:5001/wma-devops-public/wma-spring-boot-base:latest

ARG artifact_version=0.8-SNAPSHOT
ENV serverPort=7500
ENV HEALTHY_RESPONSE_CONTAINS='{"status":"UP"}'

RUN ./pull-from-artifactory.sh wma-maven-snapshots gov.usgs.owi nldi-services ${artifact_version} app.jar

HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -k "https://127.0.0.1:${serverPort}${serverContextPath}${HEALTH_CHECK_ENDPOINT}" | grep -q ${HEALTHY_RESPONSE_CONTAINS} || exit 1
