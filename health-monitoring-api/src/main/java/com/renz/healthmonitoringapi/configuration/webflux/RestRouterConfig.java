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
                .GET("/health", deviceHandler::getDeviceTypes)
                .GET("/health/{deviceName}", deviceHandler::getDevicesByName)
                .GET("/health/{deviceName}/{deviceId}", deviceHandler::getRegistersByDeviceNameAndDeviceId)
                .build();
    }
}
