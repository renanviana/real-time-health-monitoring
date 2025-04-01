package com.renz.healthmonitoring.producerdata.adapter.messaging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaDevicePublisher implements DevicePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MeterRegistry meterRegistry;

    private Counter messageCounter;
    private Map<String, Counter> counterMap = new ConcurrentHashMap<>();
    private static final String MESSAGES_PRODUCED_METRIC_NAME = "kafka_messages_produced";

    @PostConstruct
    public void init() {
        messageCounter = meterRegistry.counter(MESSAGES_PRODUCED_METRIC_NAME);
    }

    @Override
    public void publish(String topic, String key, String value) {
        kafkaTemplate.send(topic, key, value);
        messageCounter.increment();
        Counter messageTopicCounter = counterMap.computeIfAbsent(topic, k -> {
            return meterRegistry.counter("kafka_topic_" + topic + "_messages_produced");
        });
        messageTopicCounter.increment();
    }

}
