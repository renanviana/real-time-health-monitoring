package com.renz.healthmonitoring.producerdata.usecases.impl;

import com.renz.healthmonitoring.producerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateDeviceTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateDeviceTopicUseCaseImpl implements CreateDeviceTopicUseCase {

    private static final String DEVICE_TOPIC_NAME = "devices";

    private final CreateTopicUseCase createTopicUseCase;
    private final DevicePublisher devicePublisher;
    private final DeviceInformer deviceInformer;

    public CreateDeviceTopicUseCaseImpl(
            CreateTopicUseCase createTopicUseCase,
            DevicePublisher devicePublisher,
            DeviceInformer deviceInformer) {
        this.createTopicUseCase = createTopicUseCase;
        this.devicePublisher = devicePublisher;
        this.deviceInformer = deviceInformer;
    }

    @Override
    public void createDeviceTopicIfAbsentAndPublish(String key, String device) {
        if (deviceInformer.getTopicNames().contains(key)) {
            return;
        }
        createTopicUseCase.createIfAbsent(DEVICE_TOPIC_NAME);
        devicePublisher.publish(DEVICE_TOPIC_NAME, key, device);
        log.info("Push message to Kafka topic {} | key: {} | value: {}", DEVICE_TOPIC_NAME, key, device);
    }

}
