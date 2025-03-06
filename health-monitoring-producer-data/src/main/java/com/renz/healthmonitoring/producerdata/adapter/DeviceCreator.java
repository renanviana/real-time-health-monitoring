package com.renz.healthmonitoring.producerdata.adapter;

public interface DeviceCreator {
    
    void create(String topicName, Integer partitions, Short replicationFactor);

}
