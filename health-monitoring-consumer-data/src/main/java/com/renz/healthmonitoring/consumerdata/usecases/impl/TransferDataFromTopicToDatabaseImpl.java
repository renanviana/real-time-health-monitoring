package com.renz.healthmonitoring.consumerdata.usecases.impl;

import java.util.Set;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Registry;
import com.renz.healthmonitoring.consumerdata.usecases.TransferDataFromTopicToDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferDataFromTopicToDatabaseImpl implements TransferDataFromTopicToDatabase {

    private final DeviceConsumer deviceConsumer;
    private final RegistryRepository registryRepository;
    private final Set<String> topicNames;

    public TransferDataFromTopicToDatabaseImpl(
            DeviceConsumer deviceConsumer,
            RegistryRepository registryRepository,
            Set<String> topicNames) {
        this.deviceConsumer = deviceConsumer;
        this.registryRepository = registryRepository;
        this.topicNames = topicNames;
    }

    @Override
    public void transferData() {
        topicNames.forEach(topic -> deviceConsumer.consume(topic, message -> {
            registryRepository.save(new Registry(topic, message));
            log.info("Insert message in Table {} : {}", topic, message);
        }));
    }

}
