package com.renz.healthmonitoring.consumerapi.adapter;


import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeviceRepository {
    
    Flux<Device> findAll();
    
    Flux<Device> findByName(String name);

    Mono<Device> findByNameAndId(String name, String id);

}