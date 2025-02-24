package com.renz.healthmonitoringapi.adapter;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public interface DeviceHandler {
    
    Mono<ServerResponse> getDeviceTypes(ServerRequest request);

    Mono<ServerResponse> getDevicesByName(ServerRequest request);

    Mono<ServerResponse> getRegistersByDeviceNameAndDeviceId(ServerRequest request);
    
}
