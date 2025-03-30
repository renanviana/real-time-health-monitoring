package com.renz.healthmonitoring.consumerapi.adapter.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.renz.healthmonitoring.consumerapi.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.NotFoundException;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class CassandraQueriesRepositoryLegacy implements RegistryRepository {

    private final CqlSession cqlSession;

    @Override
    public Mono<List<Registry>> getBetweenDateInitalAndDateFinal(String tableName, String dateTimeInitial,
            String dateTimeFinal) {
        Long timestampInitial = convertToTimestamp(dateTimeInitial);
        Long timestampFinal = convertToTimestamp(dateTimeFinal);

        String query = QueryBuilder.selectFrom(tableName)
                .all()
                .whereColumn("timestamp").isGreaterThanOrEqualTo(QueryBuilder.bindMarker())
                .whereColumn("timestamp").isLessThanOrEqualTo(QueryBuilder.bindMarker())
                .build()
                .getQuery() + " ALLOW FILTERING";

        List<Registry> registries = new ArrayList<>();

        try {
            ResultSet resultSet = cqlSession.execute(query, timestampInitial, timestampFinal);

            for (Row row : resultSet) {
                Registry registry = new Registry(tableName, row.getString("uuid"), row.getString("data"));
                registry.setTimestamp(row.getLong("timestamp"));
                registries.add(registry);
            }

            if (registries.isEmpty()) {
                return Mono.error(new NotFoundException(
                        "Registries not found for the parameters: '" + dateTimeInitial + "' and '" + dateTimeFinal
                                + "'"));
            }

        } catch (InvalidQueryException e) {
            return Mono.error(new NotFoundException("Device does not exist"));
        }

        return Mono.fromCallable(() -> registries);
    }

    private Long convertToTimestamp(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return dateFormat.parse(dateTime).getTime();
        } catch (ParseException e) {
            throw new RuntimeException("TODO: ERROR");
        }
    }

}
