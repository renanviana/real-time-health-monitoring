package com.renz.healthmonitoring.producerdata.configuration;

import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.adapter.messaging.KafkaDevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopic;
import com.renz.healthmonitoring.producerdata.usecases.impl.TransferDataFromDeviceToTopicImpl;

@Configuration
@DependsOn({
        "emqxClient",
        "kafkaTemplate",
        "topicNames" })
public class BeanConfig {

    @Bean
    @Order(1)
    public DevicePublisher devicePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaDevicePublisher(kafkaTemplate);
    }

    @Bean
    @Order(2)
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
