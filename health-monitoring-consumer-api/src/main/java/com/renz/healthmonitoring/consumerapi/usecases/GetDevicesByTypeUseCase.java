package com.renz.healthmonitoring.consumerapi.usecases;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;

import reactor.core.publisher.Mono;

public interface GetDevicesByTypeUseCase {
    
    Mono<List<DeviceResponse>> apply(String type);

}
