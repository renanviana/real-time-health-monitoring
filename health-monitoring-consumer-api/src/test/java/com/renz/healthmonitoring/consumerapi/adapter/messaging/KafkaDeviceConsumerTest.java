package com.renz.healthmonitoring.consumerapi.adapter.messaging;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;

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

@ExtendWith(MockitoExtension.class)
public class KafkaDeviceConsumerTest {

    @Mock
    private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

    @Mock
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Mock
    private DefaultMessageHandlerMethodFactory messageHandlerMethodFactory;

    @InjectMocks
    private KafkaDeviceConsumer kafkaDeviceConsumer;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(kafkaDeviceConsumer, "groupId", "test-group");
    }

    @Test
    public void shouldCreateListenerSuccessfully() {
        String topic = "test-topic-successfully";
        String listenerId = "consumerApiListener-" + topic;
        MessageListenerContainer mockContainer = mock(MessageListenerContainer.class);
        when(kafkaListenerEndpointRegistry.getListenerContainer(listenerId))
                .thenReturn(null)
                .thenReturn(mockContainer);
        kafkaDeviceConsumer.createListener(topic, mock(Consumer.class));
        verify(mockContainer).start();
    }

    @Test
    public void shouldCreateListenerUnsuccessfully() {
        String topic = "test-topic-unsuccessfully";
        String listenerId = "consumerApiListener-" + topic;
        MessageListenerContainer mockContainer = mock(MessageListenerContainer.class);
        when(kafkaListenerEndpointRegistry.getListenerContainer(listenerId)).thenReturn(mockContainer);
        kafkaDeviceConsumer.createListener(topic, mock(Consumer.class));
        verify(kafkaListenerEndpointRegistry).getListenerContainer(listenerId);
    }

    @Test
    public void shouldProcessMessageSuccessfully() {
        String key = "test-key";
        String message = "test-message";
        Consumer<String> mockMessageHandler = mock(Consumer.class);
        KafkaDeviceConsumer.MessageProcessor messageProcessor = new KafkaDeviceConsumer.MessageProcessor(
                mockMessageHandler);
        messageProcessor.processMessage(key, message);
        verify(mockMessageHandler).accept(message);
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
