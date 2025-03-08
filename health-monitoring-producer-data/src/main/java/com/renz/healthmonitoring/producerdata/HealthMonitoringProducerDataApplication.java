package com.renz.healthmonitoring.producerdata;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopicUseCase;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class HealthMonitoringProducerDataApplication implements CommandLineRunner {

	private final TransferDataFromDeviceToTopicUseCase transferDataFromDeviceToTopicUseCase;
	
	public static void main(String[] args) {
		SpringApplication.run(HealthMonitoringProducerDataApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		transferDataFromDeviceToTopicUseCase.transferData();
	}
	
}
