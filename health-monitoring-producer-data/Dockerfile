FROM maven:3.9.9-amazoncorretto-17-debian
COPY . /usr/src/health-monitoring-producer-data
WORKDIR /usr/src/health-monitoring-producer-data
RUN apt-get update && apt-get install -y curl
RUN ./mvnw clean install -DskipTests
EXPOSE 8081
