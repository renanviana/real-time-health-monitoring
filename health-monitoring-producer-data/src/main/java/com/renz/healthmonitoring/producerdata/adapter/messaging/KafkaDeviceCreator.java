package com.renz.healthmonitoring.producerdata.adapter.messaging;

import java.util.List;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import com.renz.healthmonitoring.producerdata.adapter.DeviceCreator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KafkaDeviceCreator implements DeviceCreator {

    private final AdminClient adminClient;

    @Override
    public void create(String topicName, Integer partitions, Short replicationFactor) {
        adminClient.createTopics(List.of(new NewTopic(topicName, partitions, replicationFactor)));
        log.info("Created topic: {}", topicName);
    }

}
