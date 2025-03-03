package com.renz.healthmonitoring.consumerapi.usecases;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;

import reactor.core.publisher.Mono;

public interface GetDeviceTypesUseCase {

    Mono<List<DeviceTypeResponse>> apply();

}
