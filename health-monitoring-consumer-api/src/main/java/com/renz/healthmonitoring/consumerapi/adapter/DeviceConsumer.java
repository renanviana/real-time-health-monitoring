package com.renz.healthmonitoring.consumerapi.adapter;

import java.util.function.Consumer;

public interface DeviceConsumer {

    void consume(String topic, Consumer<String> processMessageHandler);
    
}
