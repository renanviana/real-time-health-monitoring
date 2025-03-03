package com.renz.healthmonitoring.consumerdata.configuration;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.usecases.TransferDataFromTopicToDatabase;
import com.renz.healthmonitoring.consumerdata.usecases.impl.TransferDataFromTopicToDatabaseImpl;

@Configuration
public class BeanConfig {

    @Bean
    public TransferDataFromTopicToDatabase transferDataFromTopicToDatabase(
            DeviceConsumer deviceConsumer,
            RegistryRepository registryRepository,
            Set<String> topicNames) {
        return new TransferDataFromTopicToDatabaseImpl(deviceConsumer, registryRepository, topicNames);
    }
}
