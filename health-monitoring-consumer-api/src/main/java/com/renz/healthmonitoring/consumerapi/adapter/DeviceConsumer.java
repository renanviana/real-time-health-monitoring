package com.renz.healthmonitoring.consumerapi.adapter;

import java.util.function.Consumer;

public interface DeviceConsumer {

    void createListener(String topic, Consumer<String> processMessageHandler);

}
