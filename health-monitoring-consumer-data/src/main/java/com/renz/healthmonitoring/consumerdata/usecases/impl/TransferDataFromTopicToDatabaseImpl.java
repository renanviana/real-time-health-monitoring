package com.renz.healthmonitoring.consumerdata.usecases.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Registry;
import com.renz.healthmonitoring.consumerdata.usecases.SaveDeviceUseCase;
import com.renz.healthmonitoring.consumerdata.usecases.TransferDataFromTopicToDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferDataFromTopicToDatabaseImpl implements TransferDataFromTopicToDatabase {

    private final DeviceConsumer deviceConsumer;
    private final RegistryRepository registryRepository;
    private final DeviceInformer deviceInformer;
    private final SaveDeviceUseCase saveDeviceUseCase;

    private Set<String> knownTopics = new HashSet<>();

    public TransferDataFromTopicToDatabaseImpl(
            DeviceConsumer deviceConsumer,
            RegistryRepository registryRepository,
            DeviceInformer deviceInformer,
            SaveDeviceUseCase saveDeviceUseCase) {
        this.deviceConsumer = deviceConsumer;
        this.registryRepository = registryRepository;
        this.deviceInformer = deviceInformer;
        this.saveDeviceUseCase = saveDeviceUseCase;
    }

    @Override
    public void transferData() {
        startListening();
    }

    public void startListening() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkForNewTopics, 0, 10, TimeUnit.SECONDS);
    }

    private void checkForNewTopics() {
        try {
            Set<String> currentTopics = deviceInformer.getTopicNames();
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
        String[] topicSplited = topic.split("_");
        saveDeviceUseCase.save(new Device(topicSplited[1], topicSplited[0]));
        deviceConsumer.consume(topic, message -> {
            registryRepository.createTable(topic);
            registryRepository.save(new Registry(topic, message));
            log.info("Insert message in Table {} : {}", topic, message);
        });
    }

}
