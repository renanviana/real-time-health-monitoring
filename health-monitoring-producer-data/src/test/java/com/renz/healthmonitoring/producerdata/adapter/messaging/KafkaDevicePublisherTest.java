package com.renz.healthmonitoring.producerdata.adapter.messaging;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@ExtendWith(MockitoExtension.class)
class KafkaDevicePublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter messageCounter;

    @Mock
    private Counter topicCounter;

    @InjectMocks
    private KafkaDevicePublisher kafkaDevicePublisher;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter("kafka_messages_produced")).thenReturn(messageCounter);
    }

    @Test
    void shouldPublishMessageAndIncrementCounters() {
        String topic = "test-topic";
        String key = "test-key";
        String value = "test-value";
        when(meterRegistry.counter("kafka_topic_" + topic + "_messages_produced")).thenReturn(topicCounter);
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        kafkaDevicePublisher.init();
        kafkaDevicePublisher.publish(topic, key, value);
        verify(kafkaTemplate, times(1)).send(topic, key, value);
        verify(messageCounter, times(1)).increment();
        verify(topicCounter, times(1)).increment();
    }

}
