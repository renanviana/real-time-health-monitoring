package com.renz.healthmonitoring.consumerapi.usecases;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;

import reactor.core.publisher.Mono;

public interface GetDevicesByNameUseCase {
    
    Mono<List<DeviceResponse>> apply(String name);

}
