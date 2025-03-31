package com.renz.healthmonitoring.consumerapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Health Monitoring API")
                        .version("v1")
                        .description("Real-Time Health Device Monitoring API Documentation"));
    }

}
