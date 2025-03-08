package com.renz.healthmonitoring.producerdata.usecases.impl;

import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.producerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateDeviceTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CreateDeviceTopicUseCaseImpl implements CreateDeviceTopicUseCase {

    private final CreateTopicUseCase createTopicUseCase;
    private final DevicePublisher devicePublisher;
    private final DeviceInformer deviceInformer;

    private static final String DEVICE_TOPIC_NAME = "devices";
    
    @Value("${spring.kafka.topics.devices.partitions}")
    private Integer partitions;

    @Value("${spring.kafka.topics.devices.replication-factor}")
    private Short replicationFactor;

    @Override
    public void createDeviceTopicIfAbsentAndPublish(String key, String device) {
        if (deviceInformer.getTopicNames().contains(key)) {
            return;
        }
        createTopicUseCase.createIfAbsent(DEVICE_TOPIC_NAME, partitions, replicationFactor);
        devicePublisher.publish(DEVICE_TOPIC_NAME, key, device);
        log.info("Push message to Kafka topic {} | key: {} | value: {}", DEVICE_TOPIC_NAME, key, device);
    }

}
