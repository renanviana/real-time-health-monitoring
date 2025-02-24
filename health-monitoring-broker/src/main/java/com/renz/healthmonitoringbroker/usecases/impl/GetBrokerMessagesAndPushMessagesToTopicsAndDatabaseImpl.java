package com.renz.healthmonitoringbroker.usecases.impl;

import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.renz.healthmonitoringbroker.adapter.DevicePublisher;
import com.renz.healthmonitoringbroker.adapter.RegistryRepository;
import com.renz.healthmonitoringbroker.domain.entity.cassandra.Registry;
import com.renz.healthmonitoringbroker.usecases.GetBrokerMessagesAndPushMessagesToTopicsAndDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetBrokerMessagesAndPushMessagesToTopicsAndDatabaseImpl 
        implements GetBrokerMessagesAndPushMessagesToTopicsAndDatabase {

    private final IMqttClient mqttClient;
    private final DevicePublisher devicePublisher;
    private final Set<String> topicNames;
    private final RegistryRepository registryRepository;

    public GetBrokerMessagesAndPushMessagesToTopicsAndDatabaseImpl(
            IMqttClient mqttClient,
            DevicePublisher devicePublisher,
            Set<String> topicNames,
            RegistryRepository registryRepository) {
        this.mqttClient = mqttClient;
        this.devicePublisher = devicePublisher;
        this.topicNames = topicNames;
        this.registryRepository = registryRepository;
    }

    public void apply() {
        topicNames.forEach(topicName -> {
            try {
                mqttClient.subscribe("health/".concat(topicName), (topic, msg) -> {
                    String payload = new String(msg.getPayload());
                    log.debug("Get message on MQTT topic {} : {}", topicName, payload);
                    devicePublisher.publish(topicName, payload);
                    log.debug("Save message in database {} : {}", topicName, payload);
                    registryRepository.save(new Registry(topicName, payload));
                    log.debug("Push message to Kafka topic {} : {}", topicName, payload);
                });
            } catch (MqttException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
