package com.renz.healthmonitoring.consumerapi.configuration.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@DependsOn("deviceHandler")
@RequiredArgsConstructor
public class RestRouterConfig {

    private final DeviceHandler deviceHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/devices", deviceHandler::getDevices)
                .GET("/devices/{type}", deviceHandler::getDevicesByType)
                .GET("/stream", deviceHandler::getStreamDataDevices)
                .GET("/stream/{id}", deviceHandler::getStreamDataByTopic)
                .GET("/registry/{uuid}", deviceHandler::getRegistriesBetweenDateTimeInitialAndDateTimeFinal)
                .build();
    }
}
