package com.renz.healthmonitoring.producerdata.configuration;

import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopic;
import com.renz.healthmonitoring.producerdata.usecases.impl.TransferDataFromDeviceToTopicImpl;

@Configuration
public class BeanConfig {

    @Bean
    public TransferDataFromDeviceToTopic transferDataFromDeviceToTopic(
            IMqttClient emqxClient,
            DevicePublisher devicePublisher,
            Set<String> topicNames) {
        return new TransferDataFromDeviceToTopicImpl(
                emqxClient,
                devicePublisher,
                topicNames);
    }

}
