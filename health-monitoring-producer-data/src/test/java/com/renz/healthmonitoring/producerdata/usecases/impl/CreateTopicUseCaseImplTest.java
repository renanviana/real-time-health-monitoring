package com.renz.healthmonitoring.producerdata.usecases.impl;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.renz.healthmonitoring.producerdata.adapter.DeviceCreator;
import com.renz.healthmonitoring.producerdata.adapter.DeviceInformer;

@ExtendWith(MockitoExtension.class)
public class CreateTopicUseCaseImplTest {

    @Mock
    private DeviceCreator deviceCreator;

    @Mock
    private DeviceInformer deviceInformer;

    @InjectMocks
    private CreateTopicUseCaseImpl createTopicUseCase;

    private final String TOPIC_NAME = "test-topic";
    private final Integer PARTITIONS = 3;
    private final Short REPLICATION_FACTOR = 1;

    @Test
    void shouldNotCreateTopicWhenTopicAlreadyExists() {
        when(deviceInformer.getTopicNames()).thenReturn(Set.of(TOPIC_NAME));
        createTopicUseCase.createIfAbsent(TOPIC_NAME, PARTITIONS, REPLICATION_FACTOR);
        verify(deviceCreator, never()).create(anyString(), anyInt(), anyShort());
        verify(deviceInformer, times(1)).getTopicNames();
    }

    @Test
    void shouldHandleEmptyTopicName() {
        String emptyTopicName = "";
        when(deviceInformer.getTopicNames()).thenReturn(Set.of());
        createTopicUseCase.createIfAbsent(emptyTopicName, PARTITIONS, REPLICATION_FACTOR);
        verify(deviceCreator, times(1))
                .create(emptyTopicName, PARTITIONS, REPLICATION_FACTOR);
    }

}
