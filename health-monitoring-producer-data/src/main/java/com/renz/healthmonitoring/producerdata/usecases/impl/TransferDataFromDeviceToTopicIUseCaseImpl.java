package com.renz.healthmonitoring.producerdata.usecases.impl;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopicUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferDataFromDeviceToTopicIUseCaseImpl implements TransferDataFromDeviceToTopicUseCase {

    @Value("${broker.emqx.topic}")
    private String emqxTopic;

    private final IMqttClient emqxClient;
    private final DevicePublisher devicePublisher;
    private final CreateTopicUseCase createTopicUseCase;

    public TransferDataFromDeviceToTopicIUseCaseImpl(
            IMqttClient emqxClient,
            DevicePublisher devicePublisher,
            CreateTopicUseCase createTopicUseCase) {
        this.emqxClient = emqxClient;
        this.devicePublisher = devicePublisher;
        this.createTopicUseCase = createTopicUseCase;
    }

    public void transferData() {
        try {
            emqxClient.subscribe(emqxTopic, (topic, msg) -> {
                String topicName = topic.replaceFirst("^[^/]+/(.+)", "$1").replace("/", "_");
                createTopicUseCase.createIfAbsent(topicName);
                devicePublisher.publish(topicName, new String(msg.getPayload()));
                log.info("Push message to Kafka topic {} : {}", topicName, new String(msg.getPayload()));
            });
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }

}
