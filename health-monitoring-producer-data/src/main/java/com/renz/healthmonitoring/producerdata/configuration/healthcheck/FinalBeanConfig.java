package com.renz.healthmonitoring.producerdata.configuration.healthcheck;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FinalBeanConfig {

    private final AppReadinessIndicatorConfig appReadinessIndicatorConfig;

    public FinalBeanConfig(AppReadinessIndicatorConfig appReadinessIndicatorConfig) {
        this.appReadinessIndicatorConfig = appReadinessIndicatorConfig;
        this.appReadinessIndicatorConfig.setReady();
    }

}
