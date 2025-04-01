package com.renz.healthmonitoring.consumerapi.usecases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceConsumer;
import com.renz.healthmonitoring.consumerapi.adapter.DeviceInformer;

import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class CreateKafkaListenersUseCaseImplTest {

    @Mock
    private DeviceInformer deviceInformer;

    @Mock
    private DeviceConsumer deviceConsumer;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private CreateKafkaListenersUseCaseImpl createKafkaListenersUseCase;

    @BeforeEach
    public void setUp() {
        createKafkaListenersUseCase.init();
    }

    @Test
    public void shouldReturnEmptyFluxWhenNoTopicsAreKnown() {
        ReflectionTestUtils.invokeMethod(createKafkaListenersUseCase, "checkForNewTopics");
        Flux<String> result = createKafkaListenersUseCase.getMessages();
        assertTrue(result.collectList().block().isEmpty());
    }

    @Test
    public void shouldReturnFluxWithMessagesFromTopic() {
        String topicName = "test-topic";
        String message = "Test Message";

        Map<String, Sinks.Many<String>> sinkTopicMap = (Map<String, Sinks.Many<String>>) ReflectionTestUtils
                .getField(createKafkaListenersUseCase, "sinkTopicMap");
        if (sinkTopicMap == null) {
            sinkTopicMap = new ConcurrentHashMap<>();
            ReflectionTestUtils.setField(createKafkaListenersUseCase, "sinkTopicMap", sinkTopicMap);
        }
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        sink.tryEmitNext(message);
        sinkTopicMap.put(topicName, sink);

        Set<String> knownTopics = (Set<String>) ReflectionTestUtils.getField(createKafkaListenersUseCase,
                "knownTopics");
        if (knownTopics == null) {
            knownTopics = ConcurrentHashMap.newKeySet();
            ReflectionTestUtils.setField(createKafkaListenersUseCase, "knownTopics", knownTopics);
        }
        knownTopics.add(topicName);

        Flux<String> flux = createKafkaListenersUseCase.getMessages();

        assertEquals(message, flux.blockFirst());
    }

    @Test
    public void shouldReturnEmptyFluxForUnknownTopic() {
        String unknownTopic = "unknown-topic";
        Flux<String> flux = createKafkaListenersUseCase.getMessages(unknownTopic);
        assertTrue(flux.collectList().block().isEmpty());
    }

    @Test
    public void shouldHandleSubscriptionForTopic() {
        String topicName = "test-topic";
        String message = "Test Message";

        Map<String, Sinks.Many<String>> sinkTopicMap = (Map<String, Sinks.Many<String>>) ReflectionTestUtils
                .getField(createKafkaListenersUseCase, "sinkTopicMap");
        if (sinkTopicMap == null) {
            sinkTopicMap = new ConcurrentHashMap<>();
            ReflectionTestUtils.setField(createKafkaListenersUseCase, "sinkTopicMap", sinkTopicMap);
        }
        Sinks.Many<String> topicSink = Sinks.many().multicast().onBackpressureBuffer();
        topicSink.tryEmitNext(message);
        sinkTopicMap.put(topicName, topicSink);

        Set<String> knownTopics = (Set<String>) ReflectionTestUtils.getField(createKafkaListenersUseCase,
                "knownTopics");
        if (knownTopics == null) {
            knownTopics = ConcurrentHashMap.newKeySet();
            ReflectionTestUtils.setField(createKafkaListenersUseCase, "knownTopics", knownTopics);
        }
        knownTopics.add(topicName);

        Flux<String> flux = createKafkaListenersUseCase.getMessages(topicName);

        assertEquals(message, flux.blockFirst());
    }

    @Test
    public void shouldCreateListenersForNewTopicsAndUpdateKnownTopics() {
        Set<String> currentTopics = Set.of("new-topic1", "new-topic2");

        ReflectionTestUtils.setField(createKafkaListenersUseCase, "knownTopics", currentTopics);

        when(deviceInformer.getTopicNames()).thenReturn(currentTopics);

        ReflectionTestUtils.invokeMethod(createKafkaListenersUseCase, "checkForNewTopics");

        Set<String> knownTopics = (Set<String>) ReflectionTestUtils.getField(createKafkaListenersUseCase,
                "knownTopics");

        assertTrue(knownTopics.containsAll(currentTopics));
    }

    @Test
    public void shouldReturnExistingSinkWhenSinkAlreadyExists() {
        String topicName = "existing-topic";

        Map<String, Sinks.Many<String>> sinkTopicMap = (Map<String, Sinks.Many<String>>) ReflectionTestUtils
                .getField(createKafkaListenersUseCase, "sinkTopicMap");
        if (sinkTopicMap == null) {
            sinkTopicMap = new ConcurrentHashMap<>();
            ReflectionTestUtils.setField(createKafkaListenersUseCase, "sinkTopicMap", sinkTopicMap);
        }
        Sinks.Many<String> existingSink = Sinks.many().multicast().onBackpressureBuffer();
        sinkTopicMap.put(topicName, existingSink);

        ReflectionTestUtils.invokeMethod(createKafkaListenersUseCase, "onNewListenerCreated", topicName);

        Sinks.Many<String> resultSink = sinkTopicMap.get(topicName);
        assertEquals(existingSink, resultSink);
        verify(deviceConsumer, times(0)).createListener(eq(topicName), any());
    }

    @Test
    public void shouldRemoveDevicesAndDlqTopicsAndUpdateKnownTopics() {
        Set<String> currentTopics = new HashSet<>(
                Arrays.asList("dummy-devices-topic", "dummy-dlq-topic", "valid-topic"));

        ReflectionTestUtils.setField(createKafkaListenersUseCase, "devicesTopicName", "dummy-devices-topic");
        ReflectionTestUtils.setField(createKafkaListenersUseCase, "dlqTopicName", "dummy-dlq-topic");
        ReflectionTestUtils.setField(createKafkaListenersUseCase, "knownTopics", new HashSet<String>());

        when(deviceInformer.getTopicNames()).thenReturn(currentTopics);

        ReflectionTestUtils.invokeMethod(createKafkaListenersUseCase, "checkForNewTopics");

        Set<String> updatedKnownTopics = (Set<String>) ReflectionTestUtils.getField(createKafkaListenersUseCase,
                "knownTopics");
        assertFalse(updatedKnownTopics.contains("dummy-devices-topic"));
        assertFalse(updatedKnownTopics.contains("dummy-dlq-topic"));
        assertTrue(updatedKnownTopics.contains("valid-topic"));

        verify(deviceConsumer, times(1)).createListener(eq("valid-topic"), any());
    }

    @Test
    public void shouldForwardMessageFromListenerCallback() {
        String topicName = "callback-topic";
        String emittedMessage = "Callback Message";

        Map<String, Sinks.Many<String>> sinkTopicMap = (Map<String, Sinks.Many<String>>) ReflectionTestUtils
                .getField(createKafkaListenersUseCase, "sinkTopicMap");
        if (sinkTopicMap == null) {
            sinkTopicMap = new ConcurrentHashMap<>();
            ReflectionTestUtils.setField(createKafkaListenersUseCase, "sinkTopicMap", sinkTopicMap);
        }
        sinkTopicMap.remove(topicName);

        final Consumer<String>[] capturedCallback = new Consumer[1];
        doAnswer(invocation -> {
            capturedCallback[0] = invocation.getArgument(1);
            return null;
        }).when(deviceConsumer).createListener(eq(topicName), any());

        ReflectionTestUtils.invokeMethod(createKafkaListenersUseCase, "onNewListenerCreated", topicName);

        assertNotNull(capturedCallback[0]);

        capturedCallback[0].accept(emittedMessage);

        Sinks.Many<String> sink = sinkTopicMap.get(topicName);
        String result = sink.asFlux().blockFirst();

        assertEquals(emittedMessage, result);
    }

    @Test
    public void shouldReturnEmptyFluxWhenTopicSinkIsNull() {
        String topicName = "topic-no-sink";

        Set<String> knownTopics = (Set<String>) ReflectionTestUtils.getField(createKafkaListenersUseCase,
                "knownTopics");
        if (knownTopics == null) {
            knownTopics = ConcurrentHashMap.newKeySet();
            ReflectionTestUtils.setField(createKafkaListenersUseCase, "knownTopics", knownTopics);
        }
        knownTopics.add(topicName);

        Map<String, Sinks.Many<String>> sinkTopicMap = (Map<String, Sinks.Many<String>>) ReflectionTestUtils
                .getField(createKafkaListenersUseCase, "sinkTopicMap");
        if (sinkTopicMap == null) {
            sinkTopicMap = new ConcurrentHashMap<>();
            ReflectionTestUtils.setField(createKafkaListenersUseCase, "sinkTopicMap", sinkTopicMap);
        }
        sinkTopicMap.remove(topicName);

        Flux<String> flux = createKafkaListenersUseCase.getMessages(topicName);
        Flux<String> flux2 = createKafkaListenersUseCase.getMessages();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(100))
                .thenCancel()
                .verify();

        StepVerifier.create(flux2)
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(100))
                .thenCancel()
                .verify();
    }

}
