package com.renz.healthmonitoring.producerdata.usecases.impl;

import org.springframework.stereotype.Service;

import com.renz.healthmonitoring.producerdata.adapter.DeviceCreator;
import com.renz.healthmonitoring.producerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateTopicUseCaseImpl implements CreateTopicUseCase {

    private final DeviceCreator deviceCreator;
    private final DeviceInformer deviceInformer;

    @Override
    public void createIfAbsent(String topicName, Integer partitions, Short replicationFactor) {
        if (deviceInformer.getTopicNames().contains(topicName)) {
            return;
        }
        deviceCreator.create(topicName, partitions, replicationFactor);
    }
    
}
