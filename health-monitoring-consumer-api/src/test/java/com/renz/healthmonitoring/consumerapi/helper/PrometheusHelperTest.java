package com.renz.healthmonitoring.consumerapi.helper;

import static org.mockito.Mockito.mock;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.MeterRegistry;

public class PrometheusHelperTest {

    @Test
    public void shouldRegisterGaugeSuccessfully() {
        String name = "test_gauge";
        AtomicInteger atomicInteger = new AtomicInteger(1);
        MeterRegistry mockMeterRegistry = mock(MeterRegistry.class);
        PrometheusHelper.registerGauge(name, atomicInteger, mockMeterRegistry);
    }

    @Test
    public void shouldCoverClassDefinition() {
        new PrometheusHelper(); // invokes the implicit constructor
    }

}
