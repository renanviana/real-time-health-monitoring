package com.renz.healthmonitoring.consumerdata.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import lombok.Getter;

@Configuration
public class DevicesConfig {

    @Getter
    private final Map<String, String[]> devices = new HashMap<>();

    public DevicesConfig(ConfigurableEnvironment environment) {
        final String prefix = "device";
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource.getSource() instanceof Map) {
                Map<?, ?> source = (Map<?, ?>) propertySource.getSource();
                source.forEach((key, value) -> {
                    String keyStr = key.toString();
                    if (keyStr.startsWith(prefix)) {
                        String shortKey = keyStr.replace(prefix + ".", "");
                        String[] values = value.toString().split(",");
                        devices.put(shortKey, values);
                    }
                });
            }
        }
    }

    @Bean
    public Map<String, String[]> devices() {
        return devices;
    }

}
