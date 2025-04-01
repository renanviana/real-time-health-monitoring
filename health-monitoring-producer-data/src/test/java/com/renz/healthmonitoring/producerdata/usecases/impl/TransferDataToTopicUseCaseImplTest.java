package com.renz.healthmonitoring.producerdata.usecases.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.renz.healthmonitoring.producerdata.adapter.DeviceInformer;
import com.renz.healthmonitoring.producerdata.adapter.DevicePublisher;
import com.renz.healthmonitoring.producerdata.usecases.CreateTopicUseCase;

@ExtendWith(MockitoExtension.class)
class TransferDataToTopicUseCaseImplTest {

    @Mock
    private IMqttClient emqxClient;

    @Mock
    private CreateTopicUseCase createTopicUseCase;

    @Mock
    private DeviceInformer deviceInformer;

    @Mock
    private DevicePublisher devicePublisher;

    @InjectMocks
    private TransferDataToTopicUseCaseImpl transferDataToTopicUseCase;

    private final String DEVICE_TOPIC = "devices-topic";
    private final String EMQX_TOPIC = "emqx/topic";
    private final Integer PARTITIONS = 3;
    private final Short REPLICATION_FACTOR = 1;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(transferDataToTopicUseCase, "deviceTopicName", DEVICE_TOPIC);
        ReflectionTestUtils.setField(transferDataToTopicUseCase, "partitions", PARTITIONS);
        ReflectionTestUtils.setField(transferDataToTopicUseCase, "replicationFactor", REPLICATION_FACTOR);
        ReflectionTestUtils.setField(transferDataToTopicUseCase, "emqxTopic", EMQX_TOPIC);
    }

    @Test
    void shouldHandleMqttException() throws Exception {
        doThrow(new MqttException(null)).when(emqxClient).subscribe(anyString(), any(IMqttMessageListener.class));
        transferDataToTopicUseCase.transferData();
        verify(emqxClient).subscribe(eq(EMQX_TOPIC), any(IMqttMessageListener.class));
    }

    @Test
    void shouldProcessValidTopic() throws Exception {
        ArgumentCaptor<IMqttMessageListener> listenerCaptor = ArgumentCaptor.forClass(IMqttMessageListener.class);
        String validTopic = "emqx/topic/device123/uuid456";
        MqttMessage message = new MqttMessage("payload".getBytes());
        when(deviceInformer.getTopicNames()).thenReturn(Set.of());
        transferDataToTopicUseCase.transferData();
        verify(emqxClient).subscribe(eq(EMQX_TOPIC), listenerCaptor.capture());
        listenerCaptor.getValue().messageArrived(validTopic, message);
        verify(devicePublisher).publish(eq(DEVICE_TOPIC), eq("uuid456"), eq("device123"));
        verify(createTopicUseCase).createIfAbsent(eq("uuid456"), eq(PARTITIONS), eq(REPLICATION_FACTOR));
        verify(devicePublisher).publish(eq("uuid456"), anyString(), eq("payload"));
    }

    @Test
    void shouldNotPublishWhenDeviceExists() {
        when(deviceInformer.getTopicNames()).thenReturn(Set.of("uuid123"));
        ReflectionTestUtils.invokeMethod(transferDataToTopicUseCase, "publishDeviceIfAbsent", "uuid123", "device123");
        verify(devicePublisher, never()).publish(any(), any(), any());
    }

}
