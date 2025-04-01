package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.NotFoundException;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByTypeUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetDevicesByTypeUseCaseImpl implements GetDevicesByTypeUseCase {

    private final DeviceRepository deviceRepository;

    @Override
    public Mono<List<DeviceResponse>> apply(String type) {
        Flux<Device> devices = deviceRepository.findByType(type);
        return devices.switchIfEmpty(Mono.error(new NotFoundException("Device not found")))
                .map(item -> new DeviceResponse(
                        item.getId(),
                        item.getType()))
                .collectList();
    }
}
