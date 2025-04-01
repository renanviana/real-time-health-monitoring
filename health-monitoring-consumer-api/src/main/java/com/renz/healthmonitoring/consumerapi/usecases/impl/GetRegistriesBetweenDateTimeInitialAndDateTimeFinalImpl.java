package com.renz.healthmonitoring.consumerapi.usecases.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.renz.healthmonitoring.consumerapi.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Registry;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.RegistryResponse;
import com.renz.healthmonitoring.consumerapi.helper.TableNameHelper;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistriesBetweenDateTimeInitialAndDateTimeFinal;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetRegistriesBetweenDateTimeInitialAndDateTimeFinalImpl
        implements GetRegistriesBetweenDateTimeInitialAndDateTimeFinal {

    private final RegistryRepository registryRepository;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @Override
    public Mono<List<RegistryResponse>> apply(String uuid, String dateTimeInitial, String dateTimeFinal) {
        String tableName = TableNameHelper.buildTableName(uuid);
        return convertResponse(
                registryRepository.getBetweenDateInitalAndDateFinal(tableName, dateTimeInitial, dateTimeFinal));
    }

    private Mono<List<RegistryResponse>> convertResponse(Mono<List<Registry>> registryMono) {
        return registryMono.map(registries -> registries.stream()
                .map(registry -> new RegistryResponse(
                        registry.getUuid(),
                        registry.getData(),
                        convertTimestampToDateTimeString(registry.getTimestamp())))
                .collect(Collectors.toList()))
                .map(registries -> sortingByDateTime(registries));
    }

    private String convertTimestampToDateTimeString(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
        return sdf.format(new Date(timestamp));
    }

    private List<RegistryResponse> sortingByDateTime(List<RegistryResponse> registries) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        registries.sort(Comparator.comparing(
                registry -> LocalDateTime.parse(registry.dateTime(), formatter)));
        return registries;
    }

}
