package com.renz.healthmonitoring.consumerapi.usecases;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.domain.response.webflux.RegistryResponse;

import reactor.core.publisher.Mono;

public interface GetRegistriesBetweenDateTimeInitialAndDateTimeFinal {
    
    Mono<List<RegistryResponse>> apply(String uuid, String dateTimeInitial, String dateTimeFinal);

}
