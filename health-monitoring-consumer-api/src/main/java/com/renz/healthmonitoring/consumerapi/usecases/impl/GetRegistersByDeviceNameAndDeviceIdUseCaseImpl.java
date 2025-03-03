package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistersByDeviceNameAndDeviceIdUseCase;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
public class GetRegistersByDeviceNameAndDeviceIdUseCaseImpl implements GetRegistersByDeviceNameAndDeviceIdUseCase {

    private final DeviceConsumer deviceConsumer;
    private final Map<String, Sinks.Many<String>> sinkMap = new ConcurrentHashMap<>();

    public GetRegistersByDeviceNameAndDeviceIdUseCaseImpl(DeviceConsumer deviceConsumer) {
        this.deviceConsumer = deviceConsumer;
    }

    @Override
    public Flux<String> apply(String deviceName, String deviceId) {

        final String topic = deviceName.concat("_").concat(deviceId);

        Sinks.Many<String> sink = sinkMap.computeIfAbsent(topic, key -> {
            Sinks.Many<String> newSink = Sinks.many().multicast().onBackpressureBuffer();
            deviceConsumer.consume(topic, message -> newSink.tryEmitNext(message));
            return newSink;
        });

        return sink.asFlux()
                .doOnCancel(() -> {
                    log.info("Subscriber disconnected for topic: {}", topic);
                    if (sink.currentSubscriberCount() == 0) {
                        sinkMap.remove(topic);
                    }
                })
                .doOnSubscribe(subscription -> log.info("New subscriber connected for topic: {}", topic));
    }

}
