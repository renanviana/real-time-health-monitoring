package com.renz.healthmonitoring.consumerdata.configuration.healthcheck;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FinalBeanConfig {

    private final AppReadinessIndicatorConfig appReadinessIndicatorConfig;

    public FinalBeanConfig(AppReadinessIndicatorConfig appReadinessIndicatorConfig) {
        this.appReadinessIndicatorConfig = appReadinessIndicatorConfig;
        this.appReadinessIndicatorConfig.setReady();
    }

}
