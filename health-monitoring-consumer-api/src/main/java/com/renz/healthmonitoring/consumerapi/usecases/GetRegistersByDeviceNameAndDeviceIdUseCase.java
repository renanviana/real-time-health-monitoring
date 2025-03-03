package com.renz.healthmonitoring.consumerapi.usecases;

import reactor.core.publisher.Flux;

public interface GetRegistersByDeviceNameAndDeviceIdUseCase {
    
    Flux<String> apply(String deviceName, String deviceId);

}
