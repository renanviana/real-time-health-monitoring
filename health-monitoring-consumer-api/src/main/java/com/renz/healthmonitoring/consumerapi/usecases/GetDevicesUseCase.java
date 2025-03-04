package com.renz.healthmonitoring.consumerapi.usecases;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;

import reactor.core.publisher.Mono;

public interface GetDevicesUseCase {

    Mono<List<DeviceTypeResponse>> apply();

}
