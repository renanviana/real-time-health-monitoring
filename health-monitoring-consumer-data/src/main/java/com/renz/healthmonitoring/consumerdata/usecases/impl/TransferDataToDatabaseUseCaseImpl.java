package com.renz.healthmonitoring.consumerdata.usecases.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Registry;
import com.renz.healthmonitoring.consumerdata.usecases.SaveDeviceUseCase;
import com.renz.healthmonitoring.consumerdata.usecases.TransferDataToDatabaseUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TransferDataToDatabaseUseCaseImpl implements TransferDataToDatabaseUseCase {

    private final DeviceConsumer deviceConsumer;
    private final RegistryRepository registryRepository;
    private final DeviceInformer deviceInformer;
    private final SaveDeviceUseCase saveDeviceUseCase;

    @Value("${spring.kafka.topics.devices.name}")
    private String devicesTopicName;

    @Value("${spring.kafka.topics.dlq.name}")
    private String dlqTopicName;
    
    private Set<String> knownTopics = new HashSet<>();

    @Override
    public void transferData() {
        deviceConsumer.consume(devicesTopicName, (key, message) -> {
            saveDeviceUseCase.saveIfAbsent(new Device(key, message));
            log.info("Insert on Table: {} | key: {} | value: {}", devicesTopicName, key, message);
        });
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkForNewTopics, 0, 10, TimeUnit.SECONDS);
    }

    private void checkForNewTopics() {
        try {
            Set<String> currentTopics = deviceInformer.getTopicNames();
            currentTopics.removeIf(topic -> 
                devicesTopicName.equals(topic) || dlqTopicName.equals(topic));
            Set<String> newTopics = new HashSet<>(currentTopics);
            newTopics.removeAll(knownTopics);
            if (!newTopics.isEmpty()) {
                newTopics.forEach(this::onNewTopicCreated);
                knownTopics = currentTopics;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void onNewTopicCreated(String topic) {
        deviceConsumer.consume(topic, (key, message) -> {
            registryRepository.createTable(topic);
            registryRepository.save(new Registry(topic, key, message));
            log.info("Insert on Table: {} | key: {} | value: {}", topic, key, message);
        });
    }

}
