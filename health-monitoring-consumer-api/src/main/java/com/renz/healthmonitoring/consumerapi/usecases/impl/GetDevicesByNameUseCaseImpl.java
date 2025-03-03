package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByNameUseCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GetDevicesByNameUseCaseImpl implements GetDevicesByNameUseCase {

    private final DeviceRepository deviceRepository;

    public GetDevicesByNameUseCaseImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Mono<List<DeviceResponse>> apply(String name) {
        Flux<Device> devices = deviceRepository.findByName(name);
        return devices.map(item -> new DeviceResponse(
                item.getId(), item.getName())).collectList();
    }
}
