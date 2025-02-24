package com.renz.healthmonitoringapi.usecases.impl;

import java.util.List;

import com.renz.healthmonitoringapi.adapter.DeviceRepository;
import com.renz.healthmonitoringapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoringapi.domain.response.webflux.DeviceTypeResponse;
import com.renz.healthmonitoringapi.usecases.GetDeviceTypesUseCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GetDeviceTypesUseCaseImpl implements GetDeviceTypesUseCase {
    
    private final DeviceRepository deviceRepository;

    public GetDeviceTypesUseCaseImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Mono<List<DeviceTypeResponse>> apply() {
        Flux<Device> devices = deviceRepository.findAll();
        Flux<String> deviceNames = devices.map(Device::getName).distinct();
        return deviceNames.map(name -> new DeviceTypeResponse(name)).collectList();
    }

}
