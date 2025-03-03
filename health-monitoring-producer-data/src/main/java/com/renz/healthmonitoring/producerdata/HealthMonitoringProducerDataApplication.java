package com.renz.healthmonitoring.producerdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopic;

@SpringBootApplication
public class HealthMonitoringProducerDataApplication implements CommandLineRunner {

	@Autowired
	private TransferDataFromDeviceToTopic transferDataFromDeviceToTopic;
	
	public static void main(String[] args) {
		SpringApplication.run(HealthMonitoringProducerDataApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		transferDataFromDeviceToTopic.transferData();
	}
	
}
