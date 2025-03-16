package com.renz.healthmonitoring.consumerdata;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.renz.healthmonitoring.consumerdata.usecases.TransferDataToDatabaseUseCase;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableCassandraRepositories
@RequiredArgsConstructor
public class HealthMonitoringConsumerDataApplication implements CommandLineRunner {

	private final TransferDataToDatabaseUseCase transferDataToDatabaseUseCase;

	public static void main(String[] args) {
		SpringApplication.run(HealthMonitoringConsumerDataApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		transferDataToDatabaseUseCase.transferData();
	}

}
