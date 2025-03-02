FROM maven:3.9.9-amazoncorretto-17-debian
COPY . /usr/src/health-monitoring-api
WORKDIR /usr/src/health-monitoring-api
RUN ./mvnw clean install
EXPOSE 8080
# to tests
# ENTRYPOINT [ "tail", "-f", "/dev/null" ]
