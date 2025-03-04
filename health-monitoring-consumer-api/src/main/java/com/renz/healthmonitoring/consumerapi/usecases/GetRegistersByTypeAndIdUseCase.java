package com.renz.healthmonitoring.consumerapi.usecases;

import reactor.core.publisher.Flux;

public interface GetRegistersByTypeAndIdUseCase {
    
    Flux<String> apply(String type, String id);

}
