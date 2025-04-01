package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.NotFoundException;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetDevicesUseCaseImpl implements GetDevicesUseCase {

    private final DeviceRepository deviceRepository;

    @Override
    public Mono<List<DeviceTypeResponse>> apply() {
        Flux<Device> devices = deviceRepository.findAll();
        Flux<String> types = devices
                .switchIfEmpty(Mono.error(new NotFoundException("Devices does not exist")))
                .map(Device::getType).distinct();
        return types.map(type -> new DeviceTypeResponse(type)).collectList();
    }

}
