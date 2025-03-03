package com.renz.healthmonitoring.producerdata.configuration.healthcheck;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.renz.healthmonitoring.producerdata.HealthMonitoringProducerDataApplication;

@Component
public class HealthIndicatorConfig implements HealthIndicator {

    private final AppReadinessIndicatorConfig appReadinessIndicatorConfig;

    public HealthIndicatorConfig(AppReadinessIndicatorConfig appReadinessIndicatorConfig) {
        this.appReadinessIndicatorConfig = appReadinessIndicatorConfig;
    }

    @Override
    public Health health() {
        final String applicationName = HealthMonitoringProducerDataApplication.class.getSimpleName();
        if (appReadinessIndicatorConfig.isReady()) {
            return Health.up().withDetail(applicationName, "Ready and Healthy").build();
        } else {
            return Health.down().withDetail(applicationName, "Not ready yet").build();
        }
    }

}
