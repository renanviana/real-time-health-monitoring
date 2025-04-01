package com.renz.healthmonitoring.consumerapi.usecases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.NotFoundException;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class GetDevicesUseCaseImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private GetDevicesUseCaseImpl getDevicesUseCaseImpl;

    @Test
    public void shouldReturnDistinctDeviceTypeResponseListWhenDevicesFound() {
        Device device1 = new Device("UUID1", "temp");
        Device device2 = new Device("UUID2", "ecg");
        Device device3 = new Device("UUID3", "spO2");

        when(deviceRepository.findAll()).thenReturn(Flux.just(device1, device2, device3));

        Mono<List<DeviceTypeResponse>> resultMono = getDevicesUseCaseImpl.apply();

        StepVerifier.create(resultMono)
                .assertNext(list -> {
                    assertEquals(3, list.size());

                    List<String> types = list.stream()
                            .map(DeviceTypeResponse::type)
                            .collect(Collectors.toList());

                    assertTrue(types.contains("temp"));
                    assertTrue(types.contains("ecg"));
                    assertTrue(types.contains("spO2"));
                })
                .verifyComplete();
    }

    @Test
    public void shouldReturnErrorWhenNoDevicesExist() {
        when(deviceRepository.findAll()).thenReturn(Flux.empty());
        Mono<List<DeviceTypeResponse>> resultMono = getDevicesUseCaseImpl.apply();
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Devices does not exist"))
                .verify();
    }
}
