package com.renz.healthmonitoring.consumerapi.usecases;

import reactor.core.publisher.Flux;

public interface CreateKafkaListenersUseCase {

    Flux<String> getMessages();

    Flux<String> getMessages(String topic);
    
}
