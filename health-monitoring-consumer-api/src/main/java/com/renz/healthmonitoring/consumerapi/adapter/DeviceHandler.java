package com.renz.healthmonitoring.consumerapi.adapter;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public interface DeviceHandler {
    
    Mono<ServerResponse> getDevices(ServerRequest request);

    Mono<ServerResponse> getDevicesByType(ServerRequest request);

    Mono<ServerResponse> getStreamDataDevices(ServerRequest request);

    Mono<ServerResponse> getStreamDataByTopic(ServerRequest request);

    Mono<ServerResponse> getRegistriesBetweenDateTimeInitialAndDateTimeFinal(ServerRequest request);
    
}
