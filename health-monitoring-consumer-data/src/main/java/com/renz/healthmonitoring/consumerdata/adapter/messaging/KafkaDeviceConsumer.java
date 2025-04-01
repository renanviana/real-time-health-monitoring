package com.renz.healthmonitoring.consumerdata.adapter.messaging;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceConsumer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KafkaDeviceConsumer implements DeviceConsumer {

    private final ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final DefaultMessageHandlerMethodFactory messageHandlerMethodFactory;
    private final MeterRegistry meterRegistry;

    private static Counter messageCounter;
    private static Map<String, Counter> counterMap = new ConcurrentHashMap<>();
    private static final String MESSAGES_CONSUMED_METRIC_NAME = "kafka_messages_consumed";
    private String processMessageMethodName = "processMessage"; // cannot be a constant because of unit tests

    @PostConstruct
    private void init() {
        messageCounter = meterRegistry.counter(MESSAGES_CONSUMED_METRIC_NAME);
    }

    @Value(value = "${spring.kafka.consumer.group-id}")
    private String groupId;

    @Override
    public void consume(String topic, BiConsumer<String, String> processMessageHandler) {
        String listenerId = "consumerDataListener-" + topic;
        if (kafkaListenerEndpointRegistry.getListenerContainer(listenerId) == null) {
            counterMap.put(topic, meterRegistry.counter("kafka_topic_" + topic + "_messages_consumed"));
            MethodKafkaListenerEndpoint<String, String> endpoint = new MethodKafkaListenerEndpoint<>();
            endpoint.setId(listenerId);
            endpoint.setGroupId(groupId);
            endpoint.setTopics(topic);
            endpoint.setBean(new MessageProcessor(processMessageHandler, topic));
            endpoint.setMethod(getProcessMethod());
            endpoint.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
            kafkaListenerEndpointRegistry.registerListenerContainer(endpoint, kafkaListenerContainerFactory);
            MessageListenerContainer container = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
            container.start();
        }
    }

    private Method getProcessMethod() {
        try {
            return MessageProcessor.class.getMethod(processMessageMethodName, String.class, String.class);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static class MessageProcessor {
        private final BiConsumer<String, String> messageHandler;
        private final String topic;

        public MessageProcessor(BiConsumer<String, String> messageHandler, String topic) {
            this.messageHandler = messageHandler;
            this.topic = topic;
        }

        public void processMessage(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload String message) {
            countAllMessages();
            countTopicMessage(this.topic);
            messageHandler.accept(key, message);
        }
    }

    private static void countAllMessages() {
        messageCounter.increment();
    }

    private static void countTopicMessage(String topic) {
        counterMap.get(topic).increment();
    }

}
