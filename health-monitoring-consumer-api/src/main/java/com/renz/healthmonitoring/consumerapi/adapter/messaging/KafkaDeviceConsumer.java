package com.renz.healthmonitoring.consumerapi.adapter.messaging;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaDeviceConsumer implements DeviceConsumer {

    @Value(value = "${spring.kafka.consumer.group-id}")
    private String groupId;
    private final ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final DefaultMessageHandlerMethodFactory messageHandlerMethodFactory;
    private final Map<String, AtomicInteger> activeSubscribers = new ConcurrentHashMap<>();

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
        String listenerId = "consumerApiListener-" + topic;
        activeSubscribers.compute(topic, (key, count) -> {
            if (count == null || count.get() == 0) {
                createKafkaListener(topic, listenerId, processMessageHandler);
                return new AtomicInteger(1);
            } else {
                count.incrementAndGet();
                return count;
            }
        });
    }

    @Override
    public void disconnect(String topic) {
        activeSubscribers.computeIfPresent(topic, (key, count) -> {
            int remainingSubscribers = count.decrementAndGet();
            if (remainingSubscribers <= 0) {
                removeKafkaListener(topic);
                return null;
            }
            return count;
        });
    }

    private void createKafkaListener(String topic, String listenerId, Consumer<String> processMessageHandler) {
        MethodKafkaListenerEndpoint<String, String> endpoint = new MethodKafkaListenerEndpoint<>();
        endpoint.setId(listenerId);
        endpoint.setGroupId(groupId);
        endpoint.setTopics(topic);
        endpoint.setBean(new MessageProcessor(processMessageHandler));
        endpoint.setMethod(getProcessMethod());
        endpoint.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
        kafkaListenerEndpointRegistry.registerListenerContainer(endpoint, kafkaListenerContainerFactory);
        kafkaListenerEndpointRegistry.getListenerContainer(listenerId).start();
    }

    private void removeKafkaListener(String topic) {
        String listenerId = "consumerApiListener-" + topic;
        MessageListenerContainer container = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        if (container != null) {
            container.stop();
            kafkaListenerEndpointRegistry.unregisterListenerContainer(listenerId);
        }
    }

    private Method getProcessMethod() {
        try {
            return MessageProcessor.class.getMethod("processMessage", String.class, String.class);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static class MessageProcessor {
        private final Consumer<String> messageHandler;

        public MessageProcessor(Consumer<String> messageHandler) {
            this.messageHandler = messageHandler;
        }

        public void processMessage(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload String message) {
            messageHandler.accept(message);
        }
    }
}
