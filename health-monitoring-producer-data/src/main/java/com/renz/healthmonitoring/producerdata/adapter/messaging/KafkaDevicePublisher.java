package com.renz.healthmonitoring.producerdata.adapter.messaging;

import org.springframework.kafka.core.KafkaTemplate;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KafkaDevicePublisher implements DevicePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(String topic, String key, String value) {
        kafkaTemplate.send(topic, key, value);
    }

}
