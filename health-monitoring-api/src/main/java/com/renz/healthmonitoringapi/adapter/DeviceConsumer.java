package com.renz.healthmonitoringapi.adapter;

import java.util.function.Consumer;

public interface DeviceConsumer {

    void consume(String topic, Consumer<String> processMessageHandler);
    
}
