package com.renz.healthmonitoring.producerdata.usecases.impl;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.producerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.TransferDataToTopicUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TransferDataToTopicUseCaseImpl implements TransferDataToTopicUseCase {

    @Value("${spring.kafka.topics.devices.name}")
    private String deviceTopicName;

    @Value("${spring.kafka.topics.register.partitions}")
    private Integer partitions;

    @Value("${spring.kafka.topics.register.replication-factor}")
    private Short replicationFactor;

    private final IMqttClient emqxClient;
    private final CreateTopicUseCase createTopicUseCase;
    private final DeviceInformer deviceInformer;
    private final DevicePublisher devicePublisher;

    @Value("${broker.emqx.topic}")
    private String emqxTopic;

    public void transferData() {
        try {
            emqxClient.subscribe(emqxTopic, (topic, msg) -> {
                String[] topicSplited = topic.split("/");
                String uuid = topicSplited[topicSplited.length - 1];
                String device = topicSplited[topicSplited.length - 2];
                publishDeviceIfAbsent(uuid, device);
                publishMessage(uuid, new String(msg.getPayload()));
            });
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void publishDeviceIfAbsent(String key, String device) {
        if (deviceInformer.getTopicNames().contains(key)) {
            return;
        }
        devicePublisher.publish(deviceTopicName, key, device);
        log.info("Push message to Kafka topic {} | key: {} | value: {}", deviceTopicName, key, device);
    }

    private void publishMessage(String topic, String value) {
        createTopicUseCase.createIfAbsent(topic, partitions, replicationFactor);
        String key = UUID.randomUUID().toString();
        devicePublisher.publish(topic, key, value);
        log.info("Push message to Kafka topic {} | key: {} | value: {}", topic, key, value);
    }

}
