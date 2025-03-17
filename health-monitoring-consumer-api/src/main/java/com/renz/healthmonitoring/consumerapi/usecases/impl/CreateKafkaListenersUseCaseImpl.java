package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceInformer;
import com.renz.healthmonitoring.consumerapi.usecases.CreateKafkaListenersUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@RequiredArgsConstructor
public class CreateKafkaListenersUseCaseImpl implements CreateKafkaListenersUseCase {

    @Value("${spring.kafka.topics.devices.name}")
    private String devicesTopicName;

    @Value("${spring.kafka.topics.dlq.name}")
    private String dlqTopicName;

    private final DeviceInformer deviceInformer;
    private final DeviceConsumer deviceConsumer;

    private Map<String, Sinks.Many<String>> sinkTopicMap = new ConcurrentHashMap<>();
    private Map<String, Sinks.Many<String>> sinkHostMap = new ConcurrentHashMap<>();
    private Map<String, Sinks.Many<String>> sinkHostAndTopicMap = new ConcurrentHashMap<>();
    private Set<String> knownTopics = ConcurrentHashMap.newKeySet();

    @Override
    public Flux<String> getMessages(String host) {
        checkForNewTopics();
        return sinkHostMap.computeIfAbsent(host, key -> {
            Sinks.Many<String> newSink = Sinks.many().multicast().onBackpressureBuffer(10, false);
            knownTopics.forEach(topic -> {
                Sinks.Many<String> topicSink = sinkTopicMap.get(topic);
                if (topicSink != null) {
                    topicSink.asFlux().subscribe(newSink::tryEmitNext);
                }
            });
            return newSink;
        }).asFlux()
                .doFinally(signalType -> {
                    sinkHostMap.remove(host);
                });
    }

    @Override
    public Flux<String> getMessages(String host, String topic) {
        checkForNewTopics();
        return sinkHostAndTopicMap.computeIfAbsent(host, key -> {
            Sinks.Many<String> newSink = Sinks.many().multicast().onBackpressureBuffer(10, false);
            Sinks.Many<String> topicSink = sinkTopicMap.get(topic);
            if (topicSink != null) {
                topicSink.asFlux().subscribe(newSink::tryEmitNext);
            }
            return newSink;
        }).asFlux()
                .doFinally(signalType -> {
                    sinkHostAndTopicMap.remove(host);
                });
    }

    private void checkForNewTopics() {
        try {
            Set<String> currentTopics = deviceInformer.getTopicNames();
            currentTopics.removeIf(topic -> devicesTopicName.equals(topic) || dlqTopicName.equals(topic));
            Set<String> newTopics = new HashSet<>(currentTopics);
            newTopics.removeAll(knownTopics);
            if (!newTopics.isEmpty()) {
                newTopics.forEach(this::onNewListenerCreated);
                knownTopics = currentTopics;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void onNewListenerCreated(String topic) {
        sinkTopicMap.compute(topic, (key, existingSink) -> {
            if (existingSink == null) {
                Sinks.Many<String> newSink = Sinks.many().multicast().onBackpressureBuffer(10, false);
                deviceConsumer.createListener(topic, (message) -> newSink.tryEmitNext(message));
                return newSink;
            }
            return existingSink;
        });
    }

}
