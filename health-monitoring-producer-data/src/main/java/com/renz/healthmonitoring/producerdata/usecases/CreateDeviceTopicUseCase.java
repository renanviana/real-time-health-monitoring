package com.renz.healthmonitoring.producerdata.usecases;

public interface CreateDeviceTopicUseCase {
    
    void createDeviceTopicIfAbsentAndPublish(String key, String device);

}
