package com.renz.healthmonitoring.consumerapi.usecases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceRepository;
import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.NotFoundException;
import com.renz.healthmonitoring.consumerapi.domain.entity.cassandra.Device;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class GetDevicesByTypeUseCaseImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private GetDevicesByTypeUseCaseImpl getDevicesByTypeUseCaseImpl;

    @Test
    public void shouldReturnDeviceResponseListWhenDevicesFound() {
        String type = "temp";
        Device device1 = new Device("UUID1", type);
        Device device2 = new Device("UUID2", type);

        when(deviceRepository.findByType(type)).thenReturn(Flux.just(device1, device2));

        Mono<List<DeviceResponse>> resultMono = getDevicesByTypeUseCaseImpl.apply(type);

        StepVerifier.create(resultMono)
                .assertNext(list -> {
                    assertEquals(2, list.size());

                    DeviceResponse response1 = list.get(0);
                    DeviceResponse response2 = list.get(1);
                    assertEquals("UUID1", response1.id());
                    assertEquals(type, response1.type());
                    assertEquals("UUID2", response2.id());
                    assertEquals(type, response2.type());
                })
                .verifyComplete();
    }

    @Test
    public void shouldReturnErrorWhenNoDevicesFound() {
        String type = "test-not-found";
        when(deviceRepository.findByType(type)).thenReturn(Flux.empty());

        Mono<List<DeviceResponse>> resultMono = getDevicesByTypeUseCaseImpl.apply(type);

        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Device not found"))
                .verify();
    }

}
