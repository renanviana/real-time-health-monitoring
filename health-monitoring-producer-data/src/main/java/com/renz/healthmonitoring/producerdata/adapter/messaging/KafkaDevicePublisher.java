package com.renz.healthmonitoring.producerdata.adapter.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;

public class KafkaDevicePublisher implements DevicePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaDevicePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, String key, String value) {
        kafkaTemplate.send(topic, key, value);
    }

}
