package com.renz.healthmonitoring.consumerdata.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LogConfig {

    private final LoggingSystem loggingSystem;

    @Value("${log.level}")
    private String logLevel;

    @PostConstruct
    private void init() {
        loggingSystem.setLogLevel("root", LogLevel.valueOf(logLevel));
    }
}
