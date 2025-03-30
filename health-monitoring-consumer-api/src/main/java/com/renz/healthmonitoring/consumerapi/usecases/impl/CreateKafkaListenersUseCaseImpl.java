package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceInformer;
import com.renz.healthmonitoring.consumerapi.usecases.CreateKafkaListenersUseCase;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
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
    private final MeterRegistry meterRegistry;

    private Map<String, Sinks.Many<String>> sinkTopicMap = new ConcurrentHashMap<>();
    private Map<String, Sinks.Many<String>> sinkHostMap = new ConcurrentHashMap<>();
    private Map<String, Sinks.Many<String>> sinkHostAndTopicMap = new ConcurrentHashMap<>();
    private Set<String> knownTopics = ConcurrentHashMap.newKeySet();

    private AtomicInteger activeConnections = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        Gauge.builder("kafka_stream_data_active_connections", activeConnections, AtomicInteger::get)
                .register(meterRegistry);
    }

    @Override
    public Flux<String> getMessages() {
        checkForNewTopics();
        if (knownTopics.isEmpty()) {
            return Flux.empty();
        }
        String subscribeUUID = UUID.randomUUID().toString();
        return sinkHostMap.computeIfAbsent(subscribeUUID, key -> {
            Sinks.Many<String> newSink = Sinks.many().multicast().onBackpressureBuffer(10, false);
            knownTopics.forEach(topic -> {
                Sinks.Many<String> topicSink = sinkTopicMap.get(topic);
                if (topicSink != null) {
                    topicSink.asFlux().subscribe(newSink::tryEmitNext);
                }
            });
            activeConnections.incrementAndGet();
            return newSink;
        }).asFlux().doFinally(signalType -> {
            sinkHostMap.remove(subscribeUUID);
            activeConnections.decrementAndGet();
        });
    }

    @Override
    public Flux<String> getMessages(String topic) {
        checkForNewTopics();
        if (topicNotExists(topic)) {
            return Flux.empty();
        }
        String subscribeUUID = UUID.randomUUID().toString();
        return sinkHostAndTopicMap.computeIfAbsent(subscribeUUID, key -> {
            Sinks.Many<String> newSink = Sinks.many().multicast().onBackpressureBuffer(10, false);
            Sinks.Many<String> topicSink = sinkTopicMap.get(topic);
            if (topicSink != null) {
                topicSink.asFlux().subscribe(newSink::tryEmitNext);
            }
            return newSink;
        }).asFlux().doFinally(signalType -> {
            sinkHostAndTopicMap.remove(subscribeUUID);
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

    private Boolean topicNotExists(String topic) {
        List<String> knownTopicsFiltered = knownTopics.stream().filter(t -> t.equals(topic))
                .collect(Collectors.toList());
        return knownTopicsFiltered.isEmpty();
    }

}
