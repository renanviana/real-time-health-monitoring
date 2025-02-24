package com.renz.healthmonitoringapi.usecases;

import java.util.List;

import com.renz.healthmonitoringapi.domain.response.webflux.DeviceTypeResponse;

import reactor.core.publisher.Mono;

public interface GetDeviceTypesUseCase {

    Mono<List<DeviceTypeResponse>> apply();

}
