package com.renz.healthmonitoring.consumerdata.adapter;

import java.util.function.BiConsumer;

public interface DeviceConsumer {

    void consume(String topic, BiConsumer<String, String> processMessageHandler);
    
}
