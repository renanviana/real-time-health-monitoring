package com.renz.healthmonitoringbroker.configuration.messaging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    private final Map<String, String[]> devices;
    private final KafkaAdmin kafkaAdmin;

    public KafkaTopicConfig(Map<String, String[]> devices, KafkaAdmin kafkaAdmin) {
        this.devices = devices;
        this.kafkaAdmin = kafkaAdmin;
    }

    @PostConstruct
    public void createTopics() {
        Map<String, Object> configs = kafkaAdmin.getConfigurationProperties();
        try (AdminClient adminClient = AdminClient.create(configs)) {
            List<NewTopic> topics = new ArrayList<>();
            devices.entrySet().forEach(device -> {
                String deviceName = device.getKey();
                String[] deviceIds = device.getValue();
                for (String id : deviceIds) {
                    String topicName = deviceName.concat("_").concat(id);
                    // create a topic with 3 partitions and 1 factor of replication
                    topics.add(new NewTopic(topicName, 3, (short) 1));
                }
            });
            adminClient.createTopics(topics);
            log.info("Created topics: {}", topics);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
