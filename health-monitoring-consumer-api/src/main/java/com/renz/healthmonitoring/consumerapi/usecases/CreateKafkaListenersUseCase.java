package com.renz.healthmonitoring.consumerapi.usecases;

import reactor.core.publisher.Flux;

public interface CreateKafkaListenersUseCase {

    Flux<String> getMessages(String host);

    Flux<String> getMessages(String host, String topic);
    
}
