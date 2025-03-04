package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistersByTypeAndIdUseCase;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
public class GetRegistersByTypeAndIdUseCaseImpl implements GetRegistersByTypeAndIdUseCase {

    private final DeviceConsumer deviceConsumer;
    private final Map<String, Sinks.Many<String>> sinkMap = new ConcurrentHashMap<>();

    public GetRegistersByTypeAndIdUseCaseImpl(DeviceConsumer deviceConsumer) {
        this.deviceConsumer = deviceConsumer;
    }

    @Override
    public Flux<String> apply(String type, String id) {

        // TODO: incluir validacao para type + id
        // final String topic = deviceName.concat("_").concat(id);

        Sinks.Many<String> sink = sinkMap.computeIfAbsent(id, key -> {
            Sinks.Many<String> newSink = Sinks.many().multicast().onBackpressureBuffer();
            deviceConsumer.consume(id, message -> newSink.tryEmitNext(message));
            return newSink;
        });

        return sink.asFlux()
                .doOnCancel(() -> {
                    log.info("Subscriber disconnected for topic: {}", id);
                    if (sink.currentSubscriberCount() == 0) {
                        sinkMap.remove(id);
                    }
                })
                .doOnSubscribe(subscription -> log.info("New subscriber connected for topic: {}", id));
    }

}
