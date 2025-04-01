package com.renz.healthmonitoring.consumerapi.adapter.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KafkaDeviceInformerTest {

    @Mock
    private AdminClient adminClient;

    @Mock
    private KafkaFuture<Set<String>> kafkaFuture;

    @Mock
    private ListTopicsResult listTopicsResult;

    @InjectMocks
    private KafkaDeviceInformer kafkaDeviceInformer;

    @Test
    void shouldReturnTopicNamesSuccessfully() throws Exception {
        Set<String> topics = Set.of("topic1", "topic2");
        when(adminClient.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenReturn(topics);
        Set<String> result = kafkaDeviceInformer.getTopicNames();
        assertEquals(topics, result);
        verify(adminClient, times(1)).listTopics();
    }

    @Test
    void shouldReturnEmptySetWhenExceptionOccurs() throws Exception {
        when(adminClient.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(kafkaFuture);
        when(kafkaFuture.get()).thenThrow(new ExecutionException("Error", new RuntimeException()));
        Set<String> result = kafkaDeviceInformer.getTopicNames();
        assertTrue(result.isEmpty());
    }

}
