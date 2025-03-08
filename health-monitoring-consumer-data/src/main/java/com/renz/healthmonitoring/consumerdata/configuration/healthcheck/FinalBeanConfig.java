package com.renz.healthmonitoring.consumerdata.configuration.healthcheck;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FinalBeanConfig {

    private final AppReadinessIndicatorConfig appReadinessIndicatorConfig;

    @PostConstruct
    private void init() {
        this.appReadinessIndicatorConfig.setReady();
    }

}
