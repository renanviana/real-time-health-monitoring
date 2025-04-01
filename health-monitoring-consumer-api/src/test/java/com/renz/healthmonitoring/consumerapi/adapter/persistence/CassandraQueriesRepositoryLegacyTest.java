package com.renz.healthmonitoring.consumerapi.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.NotFoundException;
import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.UnprocessableEntityException;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Registry;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class CassandraQueriesRepositoryLegacyTest {

    @Mock
    private CqlSession cqlSession;

    @InjectMocks
    private CassandraQueriesRepositoryLegacy repository;

    @Test
    public void shouldReturnRegistriesWhenDataIsFound() {
        String tableName = "t_123e4567_e89b_12d3_a456_426614174000";
        String dateTimeInitial = "2025-01-01T12:00:00";
        String dateTimeFinal = "2025-01-01T12:00:10";
        long timestampInitial = 1735743600000L;
        long timestampFinal = 1735743610000L;
        String query = "SELECT * FROM " + tableName + " WHERE timestamp>=? AND timestamp<=? ALLOW FILTERING";

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        Row mockRow = mock(Row.class);
        when(mockResultSet.iterator()).thenReturn(List.of(mockRow).iterator());
        when(mockRow.getString("uuid")).thenReturn("test-uuid");
        when(mockRow.getString("data")).thenReturn("test-data");
        when(mockRow.getLong("timestamp")).thenReturn(123456789L);

        when(cqlSession.execute(queryCaptor.capture(), eq(timestampInitial), eq(timestampFinal)))
                .thenReturn(mockResultSet);

        Mono<List<Registry>> result = repository.getBetweenDateInitalAndDateFinal(tableName, dateTimeInitial,
                dateTimeFinal);

        StepVerifier.create(result)
                .expectNextMatches(registries -> {
                    Registry registry = registries.get(0);
                    return registries.size() == 1 &&
                            "test-uuid".equals(registry.getUuid()) &&
                            "test-data".equals(registry.getData()) &&
                            registry.getTimestamp() == 123456789L;
                })
                .verifyComplete();

        assertEquals(query, queryCaptor.getValue());
    }

    @Test
    public void shouldHandleInvalidDateFormat() {
        String tableName = "test_table";
        String dateTimeInitial = "invalid-date";
        String dateTimeFinal = "2025-01-02T12:00:00";

        Mono<List<Registry>> result = repository.getBetweenDateInitalAndDateFinal(tableName, dateTimeInitial,
                dateTimeFinal);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof UnprocessableEntityException &&
                        throwable.getMessage().contains("Date Time invalid format"))
                .verify();
    }

    @Test
    public void shouldHandleEmptyResultSet() {
        String tableName = "test_table";
        String dateTimeInitial = "2025-01-01T12:00:00";
        String dateTimeFinal = "2025-01-01T12:00:10";
        String query = "SELECT * FROM " + tableName + " WHERE timestamp>=? AND timestamp<=? ALLOW FILTERING";

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.iterator()).thenReturn(new ArrayList<Row>().iterator());
        when(cqlSession.execute(eq(query), any(Long.class),
                any(Long.class))).thenReturn(mockResultSet);

        Mono<List<Registry>> result = repository.getBetweenDateInitalAndDateFinal(tableName, dateTimeInitial,
                dateTimeFinal);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().contains("Registries not found"))
                .verify();
    }

    @Test
    public void shouldHandleInvalidQueryException() {
        String tableName = "test_table";
        String dateTimeInitial = "2025-01-01T12:00:00";
        String dateTimeFinal = "2025-01-01T12:00:10";
        long timestampInitial = 1735743600000L;
        long timestampFinal = 1735743610000L;
        String query = "SELECT * FROM " + tableName + " WHERE timestamp>=? AND timestamp<=? ALLOW FILTERING";

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        InvalidQueryException mockException = mock(InvalidQueryException.class);
        when(cqlSession.execute(queryCaptor.capture(), eq(timestampInitial), eq(timestampFinal)))
                .thenThrow(mockException);

        Mono<List<Registry>> results = repository.getBetweenDateInitalAndDateFinal(tableName, dateTimeInitial,
                dateTimeFinal);

        StepVerifier.create(results)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().contains("Device does not exist"))
                .verify();

        assertEquals(query, queryCaptor.getValue());
    }

}
