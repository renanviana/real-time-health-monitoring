package com.renz.healthmonitoring.consumerapi.helper;

import java.util.concurrent.atomic.AtomicInteger;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

public class PrometheusHelper {

    public static void registerGauge(String name, AtomicInteger atomicInteger, MeterRegistry meterRegistry) {
        Gauge.builder(name, atomicInteger, AtomicInteger::get).register(meterRegistry);
    }

}
