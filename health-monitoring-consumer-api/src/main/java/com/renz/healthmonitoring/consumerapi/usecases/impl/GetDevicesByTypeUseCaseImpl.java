package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByTypeUseCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GetDevicesByTypeUseCaseImpl implements GetDevicesByTypeUseCase {

    private final DeviceRepository deviceRepository;

    public GetDevicesByTypeUseCaseImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Mono<List<DeviceResponse>> apply(String type) {
        Flux<Device> devices = deviceRepository.findByType(type);
        return devices.map(item -> new DeviceResponse(
                item.getId(), item.getType())).collectList();
    }
}
