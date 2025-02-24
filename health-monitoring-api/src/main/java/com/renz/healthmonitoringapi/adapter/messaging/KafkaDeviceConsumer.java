package com.renz.healthmonitoringapi.adapter.messaging;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Component;

import com.renz.healthmonitoringapi.adapter.DeviceConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaDeviceConsumer implements DeviceConsumer {

    @Value(value = "${spring.kafka.consumer.group-id}")
    private String groupId;

    private final ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final DefaultMessageHandlerMethodFactory messageHandlerMethodFactory;

    private Consumer<String> processMessageHandler;

    public KafkaDeviceConsumer(
            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
            ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory,
            DefaultMessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
        this.messageHandlerMethodFactory = messageHandlerMethodFactory;
    }

    @Override
    public void consume(String topic, Consumer<String> processMessageHandler) {
        this.processMessageHandler = processMessageHandler;
        String listenerId = "dynamicListener-" + topic;
        if (kafkaListenerEndpointRegistry.getListenerContainer(listenerId) == null) {
            MethodKafkaListenerEndpoint<String, String> endpoint = new MethodKafkaListenerEndpoint<>();
            endpoint.setId(listenerId);
            endpoint.setGroupId(groupId);
            endpoint.setTopics(topic);
            endpoint.setBean(this);
            endpoint.setMethod(getProcessMethod());
            endpoint.setMessageHandlerMethodFactory(messageHandlerMethodFactory);

            kafkaListenerEndpointRegistry.registerListenerContainer(endpoint, kafkaListenerContainerFactory);

            MessageListenerContainer container = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
            container.start();
        }
    }

    private Method getProcessMethod() {
        try {
            return this.getClass().getMethod("processMessage", String.class, String.class);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void processMessage(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload String message) {
        processMessageHandler.accept(message);
    }
}
