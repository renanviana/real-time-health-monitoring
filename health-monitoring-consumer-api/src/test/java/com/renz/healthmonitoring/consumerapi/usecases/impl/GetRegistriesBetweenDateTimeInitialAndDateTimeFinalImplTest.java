package com.renz.healthmonitoring.consumerapi.usecases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.renz.healthmonitoring.consumerapi.adapter.RegistryRepository;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Registry;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.RegistryResponse;
import com.renz.healthmonitoring.consumerapi.helper.TableNameHelper;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class GetRegistriesBetweenDateTimeInitialAndDateTimeFinalImplTest {

    @Mock
    private RegistryRepository registryRepository;

    @InjectMocks
    private GetRegistriesBetweenDateTimeInitialAndDateTimeFinalImpl useCase;

    @Test
    public void shouldConvertAndSortRegistryResponses() {
        String uuid = "test-uuid";
        String dateTimeInitial = "2022-01-01T00:00:00";
        String dateTimeFinal = "2022-01-02T00:00:00";
        String expectedTableName = "table-test-uuid";

        try (MockedStatic<TableNameHelper> tableNameHelperMock = Mockito.mockStatic(TableNameHelper.class)) {
            tableNameHelperMock.when(() -> TableNameHelper.buildTableName(eq(uuid)))
                    .thenReturn(expectedTableName);

            Registry registry1 = new Registry("test-uuid", "UUID1", "DATA1");
            registry1.setTimestamp(1000L);

            Registry registry2 = new Registry("test-uuid", "UUID2", "DATA2");
            registry2.setTimestamp(2000L);

            List<Registry> unsortedRegistries = List.of(registry1, registry2);

            when(registryRepository.getBetweenDateInitalAndDateFinal(
                    eq(expectedTableName), eq(dateTimeInitial), eq(dateTimeFinal)))
                    .thenReturn(Mono.just(unsortedRegistries));

            Mono<List<RegistryResponse>> resultMono = useCase.apply(uuid, dateTimeInitial, dateTimeFinal);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String expectedDate1 = sdf.format(new Date(1000L));
            String expectedDate2 = sdf.format(new Date(2000L));

            StepVerifier.create(resultMono)
                    .assertNext(list -> {
                        assertEquals(2, list.size());

                        RegistryResponse first = list.get(0);
                        RegistryResponse second = list.get(1);

                        assertEquals("UUID1", first.uuid());
                        assertEquals("DATA1", first.data());
                        assertEquals(expectedDate1, first.dateTime());

                        assertEquals("UUID2", second.uuid());
                        assertEquals("DATA2", second.data());
                        assertEquals(expectedDate2, second.dateTime());
                    })
                    .verifyComplete();
        }
    }

    @Test
    public void shouldReturnEmptyListWhenNoRegistriesFound() {
        String uuid = "test-uuid";
        String dateTimeInitial = "2022-01-01T00:00:00";
        String dateTimeFinal = "2022-01-02T00:00:00";
        String expectedTableName = "table-test-uuid";

        try (MockedStatic<TableNameHelper> tableNameHelperMock = Mockito.mockStatic(TableNameHelper.class)) {
            tableNameHelperMock.when(() -> TableNameHelper.buildTableName(eq(uuid)))
                    .thenReturn(expectedTableName);

            when(registryRepository.getBetweenDateInitalAndDateFinal(
                    eq(expectedTableName), eq(dateTimeInitial), eq(dateTimeFinal)))
                    .thenReturn(Mono.just(List.of()));

            Mono<List<RegistryResponse>> resultMono = useCase.apply(uuid, dateTimeInitial, dateTimeFinal);

            StepVerifier.create(resultMono)
                    .assertNext(list -> assertTrue(list.isEmpty()))
                    .verifyComplete();
        }
    }
}
