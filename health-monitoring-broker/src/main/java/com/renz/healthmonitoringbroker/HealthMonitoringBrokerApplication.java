package com.renz.healthmonitoringbroker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.renz.healthmonitoringbroker.usecases.GetBrokerMessagesAndPushMessagesToTopicsAndDatabase;

@SpringBootApplication
@EnableCassandraRepositories
public class HealthMonitoringBrokerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(HealthMonitoringBrokerApplication.class, args);
	}

	@Autowired
	private GetBrokerMessagesAndPushMessagesToTopicsAndDatabase getBrokerMessagesAndPushMessagesToTopicsAndDatabase;

	@Override
	public void run(String... args) throws Exception {
		getBrokerMessagesAndPushMessagesToTopicsAndDatabase.apply();
	}
	
}
