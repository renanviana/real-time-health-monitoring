package com.renz.healthmonitoring.producerdata.adapter;

public interface DevicePublisher {

    void publish(String topic, String key, String value);

}
