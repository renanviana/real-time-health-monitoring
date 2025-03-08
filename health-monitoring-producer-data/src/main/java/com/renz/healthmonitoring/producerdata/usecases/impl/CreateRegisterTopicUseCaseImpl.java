package com.renz.healthmonitoring.producerdata.usecases.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateRegisterTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CreateRegisterTopicUseCaseImpl implements CreateRegisterTopicUseCase {

    private final CreateTopicUseCase createTopicUseCase;
    private final DevicePublisher devicePublisher;

    @Value("${spring.kafka.topics.register.partitions}")
    private Integer partitions;

    @Value("${spring.kafka.topics.register.replication-factor}")
    private Short replicationFactor;

    @Override
    public void createRegisterTopicAndPublish(String topic, String value) {
        createTopicUseCase.createIfAbsent(topic, partitions, replicationFactor);
        String key = UUID.randomUUID().toString();
        devicePublisher.publish(topic, key, value);
        log.info("Push message to Kafka topic {} | key: {} | value: {}", topic, key, value);
    }

}
