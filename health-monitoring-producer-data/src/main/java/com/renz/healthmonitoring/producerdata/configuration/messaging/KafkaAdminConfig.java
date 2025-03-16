package com.renz.healthmonitoring.producerdata.configuration.messaging;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaAdminConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.topics.devices.name}")
    private String deviceTopicName;

    @Value("${spring.kafka.topics.devices.partitions}")
    private Integer partitions;
    
    @Value("${spring.kafka.topics.devices.replication-factor}")
    private Integer replicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = kafkaAdmin().getConfigurationProperties();
        return AdminClient.create(configs);
    }

    @Bean
    public NewTopic devicesTopic() {
        return TopicBuilder.name(deviceTopicName)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }

}
