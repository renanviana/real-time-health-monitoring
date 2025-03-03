FROM maven:3.9.9-amazoncorretto-17-debian
COPY . /usr/src/health-monitoring-consumer-api
WORKDIR /usr/src/health-monitoring-consumer-api
RUN ./mvnw clean install
EXPOSE 8080
