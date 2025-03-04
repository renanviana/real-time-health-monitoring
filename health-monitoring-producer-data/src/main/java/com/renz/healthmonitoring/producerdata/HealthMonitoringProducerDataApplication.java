package com.renz.healthmonitoring.producerdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopicUseCase;

@SpringBootApplication
public class HealthMonitoringProducerDataApplication implements CommandLineRunner {

	@Autowired
	private TransferDataFromDeviceToTopicUseCase transferDataFromDeviceToTopicUseCase;
	
	public static void main(String[] args) {
		SpringApplication.run(HealthMonitoringProducerDataApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		transferDataFromDeviceToTopicUseCase.transferData();
	}
	
}
