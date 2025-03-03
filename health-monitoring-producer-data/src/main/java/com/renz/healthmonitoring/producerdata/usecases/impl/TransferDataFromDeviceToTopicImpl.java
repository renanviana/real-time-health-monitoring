package com.renz.healthmonitoring.producerdata.usecases.impl;

import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferDataFromDeviceToTopicImpl implements TransferDataFromDeviceToTopic {

    private final IMqttClient emqxClient;
    private final DevicePublisher devicePublisher;
    private final Set<String> topicNames;

    public TransferDataFromDeviceToTopicImpl(
            IMqttClient emqxClient,
            DevicePublisher devicePublisher,
            Set<String> topicNames) {
        this.emqxClient = emqxClient;
        this.devicePublisher = devicePublisher;
        this.topicNames = topicNames;
    }

    public void transferData() {
        topicNames.forEach(topicName -> {
            try {
                emqxClient.subscribe("health/".concat(topicName), (topic, msg) -> {
                    devicePublisher.publish(topicName, new String(msg.getPayload()));
                    log.info("Push message to Kafka topic {} : {}", topicName, new String(msg.getPayload()));
                });
            } catch (MqttException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
