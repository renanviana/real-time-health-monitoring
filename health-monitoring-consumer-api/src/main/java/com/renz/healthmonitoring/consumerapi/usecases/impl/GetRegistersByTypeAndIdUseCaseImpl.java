package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistersByTypeAndIdUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@RequiredArgsConstructor
public class GetRegistersByTypeAndIdUseCaseImpl implements GetRegistersByTypeAndIdUseCase {

    private final DeviceConsumer deviceConsumer;
    private Map<String, Sinks.Many<String>> sinkMap = new ConcurrentHashMap<>();

    @Override
    public Flux<String> apply(String type, String id) {

        Sinks.Many<String> sink = sinkMap.compute(id, (key, existingSink) -> {
            if (existingSink == null || existingSink.currentSubscriberCount() == 0) {
                Sinks.Many<String> newSink = Sinks.many().multicast().onBackpressureBuffer();
                deviceConsumer.consume(id, message -> newSink.tryEmitNext(message));
                return newSink;
            }
            return existingSink;
        });

        return sink.asFlux()
                .doOnCancel(() -> {
                    if (sink.currentSubscriberCount() == 1) {
                        sinkMap.remove(id);
                        deviceConsumer.disconnect(id);
                    }
                });

    }
}
