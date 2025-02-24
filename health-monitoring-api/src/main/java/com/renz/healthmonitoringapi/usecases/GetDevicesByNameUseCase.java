package com.renz.healthmonitoringapi.usecases;

import java.util.List;

import com.renz.healthmonitoringapi.domain.response.webflux.DeviceResponse;

import reactor.core.publisher.Mono;

public interface GetDevicesByNameUseCase {
    
    Mono<List<DeviceResponse>> apply(String name);

}
