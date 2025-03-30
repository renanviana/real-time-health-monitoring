package com.renz.healthmonitoring.consumerapi.adapter;

import java.util.List;

import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Registry;

import reactor.core.publisher.Mono;

public interface RegistryRepository {

    Mono<List<Registry>> getBetweenDateInitalAndDateFinal(String table, String dateTimeInitial, String dateTimeFinal);

}
