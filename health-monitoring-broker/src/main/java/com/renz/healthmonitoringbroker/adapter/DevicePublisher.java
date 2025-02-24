package com.renz.healthmonitoringbroker.adapter;

public interface DevicePublisher {

    void publish(String topic, String value);

}
