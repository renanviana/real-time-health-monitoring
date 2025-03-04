package com.renz.healthmonitoring.producerdata.configuration.healthcheck;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({
        "kafkaAdminConfig",
        "kafkaProducerConfig",
        "emqxConfig",
        "beanConfig",
        "logConfig" })
public class AppReadinessIndicatorConfig {

    private boolean ready = false;

    public void setReady() {
        this.ready = true;
    }

    public boolean isReady() {
        return ready;
    }

}
