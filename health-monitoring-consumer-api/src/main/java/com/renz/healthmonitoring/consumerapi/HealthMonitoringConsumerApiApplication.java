package com.renz.healthmonitoring.consumerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

@SpringBootApplication
@EnableReactiveCassandraRepositories
public class HealthMonitoringConsumerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthMonitoringConsumerApiApplication.class, args);
	}

}
