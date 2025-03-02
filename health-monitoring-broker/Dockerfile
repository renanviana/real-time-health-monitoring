FROM maven:3.9.9-amazoncorretto-17-debian
COPY . /usr/src/health-monitoring-broker
WORKDIR /usr/src/health-monitoring-broker
RUN apt-get update && apt-get install -y curl
RUN ./mvnw clean install
