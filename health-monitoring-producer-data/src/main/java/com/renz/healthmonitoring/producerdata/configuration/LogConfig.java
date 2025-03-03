package com.renz.healthmonitoring.producerdata.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class LogConfig {
    
    @Autowired
    private LoggingSystem loggingSystem;

    @Value("${log.level}")
    private String logLevel;

    @PostConstruct
    public void configureLogging() {
        loggingSystem.setLogLevel("root", LogLevel.valueOf(logLevel));
    }
}
