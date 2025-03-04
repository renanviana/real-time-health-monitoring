package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesUseCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GetDevicesUseCaseImpl implements GetDevicesUseCase {
    
    private final DeviceRepository deviceRepository;

    public GetDevicesUseCaseImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Mono<List<DeviceTypeResponse>> apply() {
        Flux<Device> devices = deviceRepository.findAll();
        Flux<String> types = devices.map(Device::getType).distinct();
        return types.map(type -> new DeviceTypeResponse(type)).collectList();
    }

}
