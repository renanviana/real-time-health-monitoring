package com.renz.healthmonitoring.producerdata.usecases;

public interface CreateRegisterTopicUseCase {
    
    void createRegisterTopicAndPublish(String topic, String message);

}
