package com.renz.healthmonitoring.consumerdata.configuration.messaging;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaAdmin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@DependsOn("kafkaAdminConfig")
public class KafkaTopicConfig {

    private final KafkaAdmin kafkaAdmin;

    public KafkaTopicConfig(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Bean
    public Set<String> topicNames() {
        Map<String, Object> configs = kafkaAdmin.getConfigurationProperties();
        try (AdminClient adminClient = AdminClient.create(configs)) {
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            return listTopicsResult.names().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            return new HashSet<String>();
        }
    }

}
