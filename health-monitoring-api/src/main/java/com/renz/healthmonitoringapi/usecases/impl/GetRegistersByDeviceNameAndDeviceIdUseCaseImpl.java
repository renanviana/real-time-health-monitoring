package com.renz.healthmonitoringapi.usecases.impl;

import com.renz.healthmonitoringapi.adapter.DeviceConsumer;
import com.renz.healthmonitoringapi.usecases.GetRegistersByDeviceNameAndDeviceIdUseCase;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
public class GetRegistersByDeviceNameAndDeviceIdUseCaseImpl implements GetRegistersByDeviceNameAndDeviceIdUseCase {

    private final Sinks.Many<String> sink = Sinks.many().replay().latest();
    private final DeviceConsumer deviceConsumer;

    public GetRegistersByDeviceNameAndDeviceIdUseCaseImpl(DeviceConsumer deviceConsumer) {
        this.deviceConsumer = deviceConsumer;
    }

    @Override
    public Flux<String> apply(String deviceName, String deviceId) {
        final String topic = deviceName.concat("_").concat(deviceId);
        deviceConsumer.consume(topic, message -> sink.tryEmitNext(message));
        return sink.asFlux()
                .doOnCancel(() -> log.debug("Subscriber disconnected"))
                .doOnSubscribe(subscription -> log.debug("New subscriber connected"));
    }

}
