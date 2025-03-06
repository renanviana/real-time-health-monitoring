package com.renz.healthmonitoring.producerdata.adapter.messaging;

import java.util.List;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import com.renz.healthmonitoring.producerdata.adapter.DeviceCreator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaDeviceCreator implements DeviceCreator {

    private final AdminClient adminClient;

    public KafkaDeviceCreator(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @Override
    public void create(String topicName, Integer partitions, Short replicationFactor) {
        adminClient.createTopics(List.of(new NewTopic(topicName, partitions, replicationFactor)));
        log.info("Created topic: {}", topicName);
    }

}
