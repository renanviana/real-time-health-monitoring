package com.renz.healthmonitoring.consumerdata.configuration.healthcheck;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({
        "kafkaAdminConfig",
        "kafkaConsumerConfig",
        "kafkaTopicConfig",
        "cassandraConfig",
        "devicesConfig",
        "logConfig",
        "beanConfig" })
public class AppReadinessIndicatorConfig {

    private boolean ready = false;

    public void setReady() {
        this.ready = true;
    }

    public boolean isReady() {
        return ready;
    }

}
