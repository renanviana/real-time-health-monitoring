package com.renz.healthmonitoring.consumerdata.usecases.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.renz.healthmonitoring.consumerdata.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.consumerdata.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerdata.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerdata.usecases.SaveDeviceUseCase;

@ExtendWith(MockitoExtension.class)
class TransferDataToDatabaseUseCaseImplTest {

    @Mock
    private DeviceConsumer deviceConsumer;

    @Mock
    private RegistryRepository registryRepository;

    @Mock
    private DeviceInformer deviceInformer;

    @Mock
    private SaveDeviceUseCase saveDeviceUseCase;

    @InjectMocks
    private TransferDataToDatabaseUseCaseImpl useCase;

    private ScheduledExecutorService scheduler;

    @BeforeEach
    void setUp() {
        scheduler = mock(ScheduledExecutorService.class);
        ReflectionTestUtils.setField(useCase, "scheduler", scheduler);
        ReflectionTestUtils.setField(useCase, "devicesTopicName", "devices-topic");
        ReflectionTestUtils.setField(useCase, "dlqTopicName", "dlq-topic");
    }

    @AfterEach
    void tearDown() {
        scheduler.shutdownNow();
    }

    @Test
    void shouldIgnoreKnownTopics() throws Exception {
        Set<String> topics = new HashSet<>();
        topics.add("devices-topic");
        topics.add("dlq-topic");
        topics.add("new-topic");
        when(deviceInformer.getTopicNames()).thenReturn(topics);
        ReflectionTestUtils.setField(useCase, "knownTopics", new HashSet<>());
        ReflectionTestUtils.invokeMethod(useCase, "checkForNewTopics");
        verify(deviceInformer).getTopicNames();
        verify(deviceConsumer).consume(eq("new-topic"), any());
    }

    @Test
    void shouldHandleException() throws Exception {
        when(deviceInformer.getTopicNames()).thenThrow(new RuntimeException("Error"));
        ReflectionTestUtils.invokeMethod(useCase, "checkForNewTopics");
        verify(deviceInformer).getTopicNames();
    }

    @Test
    void shouldRegisterConsumerAndExecuteLambda() {
        String topic = "new-topic";
        String key = "test-key";
        String value = "test-value";
        String expectedTableName = "t_new_topic";
        ArgumentCaptor<BiConsumer<String, String>> consumerCaptor = ArgumentCaptor.forClass(BiConsumer.class);
        ReflectionTestUtils.invokeMethod(useCase, "onNewTopicCreated", topic);
        verify(deviceConsumer).consume(eq(topic), consumerCaptor.capture());
        consumerCaptor.getValue().accept(key, value);
        InOrder inOrder = inOrder(registryRepository);
        inOrder.verify(registryRepository).createTable(topic);
        inOrder.verify(registryRepository).save(argThat(registry -> registry.getTableName().equals(expectedTableName) &&
                registry.getUuid().equals(key) &&
                registry.getData().equals(value)));
    }

    @Test
    void shouldRegisterDeviceConsumer() {
        String key = "device-key";
        String message = "device-message";
        doNothing().when(saveDeviceUseCase).saveIfAbsent(any(Device.class));
        ArgumentCaptor<BiConsumer<String, String>> consumerCaptor = ArgumentCaptor.forClass(BiConsumer.class);
        useCase.transferData();
        verify(deviceConsumer).consume(eq("devices-topic"), consumerCaptor.capture());
        BiConsumer<String, String> deviceConsumer = consumerCaptor.getValue();
        deviceConsumer.accept(key, message);

        verify(saveDeviceUseCase, times(1)).saveIfAbsent(argThat(device -> device.getId().equals(key) &&
                device.getType().equals(message)));
        verify(scheduler).scheduleAtFixedRate(any(), eq(0L), eq(10L), eq(TimeUnit.SECONDS));
    }

    @Test
    void shouldNotProcessWhenNoNewTopics() throws Exception {
        Set<String> currentTopics = new HashSet<>(Arrays.asList("topic1", "topic2"));
        Set<String> knownTopics = new HashSet<>(currentTopics);
        when(deviceInformer.getTopicNames()).thenReturn(currentTopics);
        ReflectionTestUtils.setField(useCase, "knownTopics", knownTopics);
        ReflectionTestUtils.invokeMethod(useCase, "checkForNewTopics");
        verify(deviceConsumer, never()).consume(anyString(), any());
        assertThat(ReflectionTestUtils.getField(useCase, "knownTopics")).isEqualTo(currentTopics);
    }

}
