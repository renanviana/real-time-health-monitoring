package com.renz.healthmonitoringapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

@SpringBootApplication
@EnableReactiveCassandraRepositories
public class HealthMonitoringApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthMonitoringApiApplication.class, args);
	}

}
