package com.renz.healthmonitoringapi.configuration.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.renz.healthmonitoringapi.adapter.webflux.WebFluxDeviceHandler;

@Configuration
public class RestRouterConfig {

    private final WebFluxDeviceHandler deviceHandler;

    public RestRouterConfig(WebFluxDeviceHandler deviceHandler) {
        this.deviceHandler = deviceHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/device", deviceHandler::getDeviceTypes)
                .GET("/device/{deviceName}", deviceHandler::getDevicesByName)
                .GET("/device/{deviceName}/{deviceId}", deviceHandler::getRegistersByDeviceNameAndDeviceId)
                .build();
    }
}
