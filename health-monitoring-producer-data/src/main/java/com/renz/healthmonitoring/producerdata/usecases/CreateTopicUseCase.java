package com.renz.healthmonitoring.producerdata.usecases;

public interface CreateTopicUseCase {
    
    void createIfAbsent(String topicName);

}
