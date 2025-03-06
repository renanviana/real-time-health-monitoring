package com.renz.healthmonitoring.producerdata.usecases.impl;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.producerdata.usecases.CreateDeviceTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.CreateRegisterTopicUseCase;
import com.renz.healthmonitoring.producerdata.usecases.TransferDataFromDeviceToTopicUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferDataFromDeviceToTopicIUseCaseImpl implements TransferDataFromDeviceToTopicUseCase {

    @Value("${broker.emqx.topic}")
    private String emqxTopic;

    private final IMqttClient emqxClient;
    private final CreateDeviceTopicUseCase createDeviceTopicUseCase;
    private final CreateRegisterTopicUseCase createRegisterTopicUseCase;

    public TransferDataFromDeviceToTopicIUseCaseImpl(
            IMqttClient emqxClient,
            CreateDeviceTopicUseCase createDeviceTopicUseCase,
            CreateRegisterTopicUseCase createRegisterTopicUseCase) {
        this.emqxClient = emqxClient;
        this.createDeviceTopicUseCase = createDeviceTopicUseCase;
        this.createRegisterTopicUseCase = createRegisterTopicUseCase;
    }

    public void transferData() {
        try {
            emqxClient.subscribe(emqxTopic, (topic, msg) -> {
                String[] topicSplited = topic.split("/");
                String uuid = topicSplited[topicSplited.length - 1];
                String device = topicSplited[topicSplited.length - 2];
                createDeviceTopicUseCase.createDeviceTopicIfAbsentAndPublish(uuid, device);
                createRegisterTopicUseCase.createRegisterTopicAndPublish(uuid, new String(msg.getPayload()));
            });
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }

}
