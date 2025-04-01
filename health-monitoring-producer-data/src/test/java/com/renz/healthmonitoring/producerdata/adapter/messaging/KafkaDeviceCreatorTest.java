package com.renz.healthmonitoring.producerdata.adapter.messaging;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaDeviceCreatorTest {

    @Mock
    private AdminClient adminClient;

    @InjectMocks
    private KafkaDeviceCreator kafkaDeviceCreator;

    @Test
    void shouldCreateTopicSuccessfully() {
        String topicName = "test-topic";
        int partitions = 3;
        short replicationFactor = 1;
        kafkaDeviceCreator.create(topicName, partitions, replicationFactor);
        verify(adminClient, times(1)).createTopics(List.of(new NewTopic(topicName, partitions, replicationFactor)));
        verifyNoMoreInteractions(adminClient);
    }
}
