package com.renz.healthmonitoring.producerdata.usecases.impl;

import java.util.UUID;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateRegisterTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateRegisterTopicUseCaseImpl implements CreateRegisterTopicUseCase {

    private final CreateTopicUseCase createTopicUseCase;
    private final DevicePublisher devicePublisher;

    public CreateRegisterTopicUseCaseImpl(
            CreateTopicUseCase createTopicUseCase,
            DevicePublisher devicePublisher) {
        this.createTopicUseCase = createTopicUseCase;
        this.devicePublisher = devicePublisher;
    }

    @Override
    public void createRegisterTopicAndPublish(String topic, String value) {
        createTopicUseCase.createIfAbsent(topic);
        String key = UUID.randomUUID().toString();
        devicePublisher.publish(topic, key, value);
        log.info("Push message to Kafka topic {} | key: {} | value: {}", topic, key, value);
    }

}
