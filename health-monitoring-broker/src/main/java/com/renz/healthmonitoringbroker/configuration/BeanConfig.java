package com.renz.healthmonitoringbroker.configuration;

import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renz.healthmonitoringbroker.adapter.DevicePublisher;
import com.renz.healthmonitoringbroker.adapter.RegistryRepository;
import com.renz.healthmonitoringbroker.usecases.GetBrokerMessagesAndPushMessagesToTopicsAndDatabase;
import com.renz.healthmonitoringbroker.usecases.impl.GetBrokerMessagesAndPushMessagesToTopicsAndDatabaseImpl;

@Configuration
public class BeanConfig {

    @Bean
    public GetBrokerMessagesAndPushMessagesToTopicsAndDatabase healthMonitoringUseCase(
            IMqttClient emqxClient,
            DevicePublisher devicePublisher,
            Set<String> topicNames,
            RegistryRepository registryRepository) {
        return new GetBrokerMessagesAndPushMessagesToTopicsAndDatabaseImpl(
            emqxClient,
                devicePublisher,
                topicNames,
                registryRepository);
    }

}
