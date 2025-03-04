package com.renz.healthmonitoring.producerdata.usecases.impl;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.producerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopicUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferDataFromDeviceToTopicIUseCaseImpl implements TransferDataFromDeviceToTopicUseCase {

    private static final String DEVICE_TOPIC_NAME = "devices";

    @Value("${broker.emqx.topic}")
    private String emqxTopic;

    private final IMqttClient emqxClient;
    private final DevicePublisher devicePublisher;
    private final CreateTopicUseCase createTopicUseCase;
    private final DeviceInformer deviceInformer;

    public TransferDataFromDeviceToTopicIUseCaseImpl(
            IMqttClient emqxClient,
            DevicePublisher devicePublisher,
            CreateTopicUseCase createTopicUseCase,
            DeviceInformer deviceInformer) {
        this.emqxClient = emqxClient;
        this.devicePublisher = devicePublisher;
        this.createTopicUseCase = createTopicUseCase;
        this.deviceInformer = deviceInformer;
    }

    public void transferData() {
        try {
            emqxClient.subscribe(emqxTopic, (topic, msg) -> {
                String[] topicSplited = topic.split("/");
                String uuid = topicSplited[topicSplited.length - 1];
                String device = topicSplited[topicSplited.length - 2];
                createDeviceTopicIfAbsentAndPublish(uuid, device);
                createTopicUseCase.createIfAbsent(uuid);
                String key = UUID.randomUUID().toString();
                String value = new String(msg.getPayload());
                devicePublisher.publish(uuid, key, value);
                log.info("Push message to Kafka topic {} | key: {} | value: {}", uuid, key, value);
            });
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void createDeviceTopicIfAbsentAndPublish(String key, String device) {
        if (deviceInformer.getTopicNames().contains(key)) {
            return;
        }
        createTopicUseCase.createIfAbsent(DEVICE_TOPIC_NAME);
        devicePublisher.publish(DEVICE_TOPIC_NAME, key, device);
        log.info("Push message to Kafka topic {} | key: {} | value: {}", DEVICE_TOPIC_NAME, key, device);
    }

}
