package com.renz.healthmonitoring.producerdata.adapter.messaging;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;

@Component
public class KafkaDevicePublisher implements DevicePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaDevicePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, String value) {
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), value);
    }

}
