package com.renz.healthmonitoring.consumerdata.configuration.messaging;

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

    @Value(value = "${spring.kafka.topics.dlq.name}")
    private String dlqTopicName;

    @Value(value = "${spring.kafka.topics.dlq.partitions}")
    private Integer dlqTopicPartitions;

    @Value(value = "${spring.kafka.topics.dlq.replication-factor}")
    private Integer dlqTopicReplicationFactor;

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
    public NewTopic dlqTopic() {
        return TopicBuilder.name(dlqTopicName)
                .partitions(dlqTopicPartitions)
                .replicas(dlqTopicReplicationFactor)
                .build();
    }

}
