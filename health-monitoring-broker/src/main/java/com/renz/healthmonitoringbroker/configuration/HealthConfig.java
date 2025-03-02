package com.renz.healthmonitoringbroker.configuration;

import java.util.List;
import java.util.Set;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.renz.healthmonitoringbroker.HealthMonitoringBrokerApplication;
import com.renz.healthmonitoringbroker.domain.entity.cassandra.Device;

@Component
public class HealthConfig implements HealthIndicator {

    private final Set<String> topicNames;
    private final List<Device> cassandraDevices;

    public HealthConfig(Set<String> topicNames, List<Device> cassandraDevices) {
        this.topicNames = topicNames;
        this.cassandraDevices = cassandraDevices;
    }

    @Override
    public Health health() {
        boolean isBrokerReady = checkIfBrokerIsReady();
        final String key = HealthMonitoringBrokerApplication.class.getSimpleName();
        if (isBrokerReady) {
            return Health.up().withDetail(key, "Ready and Healthy").build();
        } else {
            return Health.down().withDetail(key, "Not ready yet").build();
        }
    }

    private boolean checkIfBrokerIsReady() {
        return !topicNames.isEmpty() && !cassandraDevices.isEmpty();
    }
    
}
