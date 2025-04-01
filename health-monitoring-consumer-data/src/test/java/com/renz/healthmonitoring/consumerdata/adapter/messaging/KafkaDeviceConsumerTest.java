package com.renz.healthmonitoring.consumerdata.adapter.messaging;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.test.util.ReflectionTestUtils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@ExtendWith(MockitoExtension.class)
public class KafkaDeviceConsumerTest {

    @Mock
    private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

    @Mock
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Mock
    private DefaultMessageHandlerMethodFactory messageHandlerMethodFactory;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private KafkaDeviceConsumer kafkaDeviceConsumer;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(kafkaDeviceConsumer, "groupId", "test-group");
    }

    @Test
    public void shouldConsumeSuccessfully() {
        String topic = "test-topic-successfully";
        String listenerId = "consumerDataListener-" + topic;
        MessageListenerContainer mockContainer = mock(MessageListenerContainer.class);
        when(kafkaListenerEndpointRegistry.getListenerContainer(listenerId))
                .thenReturn(null)
                .thenReturn(mockContainer);
        when(meterRegistry.counter(anyString())).thenReturn(mock(Counter.class));
        kafkaDeviceConsumer.consume(topic, mock(BiConsumer.class));
        verify(mockContainer).start();
    }

    @Test
    public void shouldConsumeUnsuccessfully() {
        String topic = "test-topic-unsuccessfully";
        String listenerId = "consumerDataListener-" + topic;
        MessageListenerContainer mockContainer = mock(MessageListenerContainer.class);
        when(kafkaListenerEndpointRegistry.getListenerContainer(listenerId)).thenReturn(mockContainer);
        kafkaDeviceConsumer.consume(topic, mock(BiConsumer.class));
        verify(kafkaListenerEndpointRegistry).getListenerContainer(listenerId);
    }

    @Test
    public void shouldProcessMessageSuccessfully() {
        String topic = "test-topic-process-message-successfully";
        String key = "test-key";
        String message = "test-message";

        Counter mockGlobalCounter = mock(Counter.class);
        Counter mockTopicCounter = mock(Counter.class);

        when(meterRegistry.counter("kafka_messages_consumed")).thenReturn(mockGlobalCounter);

        ReflectionTestUtils.invokeMethod(kafkaDeviceConsumer, "init");

        Map<String, Counter> mockCounterMap = new ConcurrentHashMap<>();
        mockCounterMap.put(topic, mockTopicCounter);
        ReflectionTestUtils.setField(KafkaDeviceConsumer.class, "counterMap", mockCounterMap);

        BiConsumer<String, String> mockMessageHandler = mock(BiConsumer.class);
        KafkaDeviceConsumer.MessageProcessor messageProcessor = new KafkaDeviceConsumer.MessageProcessor(
                mockMessageHandler, topic);

        messageProcessor.processMessage(key, message);

        verify(mockGlobalCounter).increment();
        verify(mockTopicCounter).increment();
        verify(mockMessageHandler).accept(key, message);
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenGetProcessMethodFails() {
        try {
            ReflectionTestUtils.setField(kafkaDeviceConsumer, "processMessageMethodName", "methodNotFound");
            ReflectionTestUtils.invokeMethod(kafkaDeviceConsumer, "getProcessMethod");
            fail(" The exception RuntimeException should be throw!");
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }

}
