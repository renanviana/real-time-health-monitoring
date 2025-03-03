package com.renz.healthmonitoring.consumerdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.renz.healthmonitoring.consumerdata.usecases.TransferDataFromTopicToDatabase;

@SpringBootApplication
@EnableCassandraRepositories
public class HealthMonitoringConsumerDataApplication implements CommandLineRunner {

	@Autowired
	private TransferDataFromTopicToDatabase transferDataFromTopicToDatabase;

	public static void main(String[] args) {
		SpringApplication.run(HealthMonitoringConsumerDataApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		transferDataFromTopicToDatabase.transferData();
	}

}
