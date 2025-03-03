package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;
import com.renz.healthmonitoring.consumerapi.usecases.GetDeviceTypesUseCase;

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
