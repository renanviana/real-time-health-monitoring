package com.renz.healthmonitoring.producerdata.adapter.messaging;

import java.util.List;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.producerdata.adapter.DeviceCreator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaDeviceCreator implements DeviceCreator {

    @Value("${spring.kafka.topics.partitions}")
    private Integer partitions;

    @Value("${spring.kafka.topics.replication-factor}")
    private Short replicationFactor;

    private final AdminClient adminClient;

    public KafkaDeviceCreator(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @Override
    public void create(String topicName) {
        adminClient.createTopics(List.of(new NewTopic(topicName, partitions, replicationFactor)));
        log.info("Created topic: {}", topicName);
    }

}
