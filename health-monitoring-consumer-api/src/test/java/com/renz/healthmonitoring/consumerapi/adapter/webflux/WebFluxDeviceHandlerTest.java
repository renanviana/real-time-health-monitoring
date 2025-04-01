package com.renz.healthmonitoring.consumerapi.adapter.webflux;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.BadRequestException;
import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.NotFoundException;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.RegistryResponse;
import com.renz.healthmonitoring.consumerapi.usecases.CreateKafkaListenersUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByTypeUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistriesBetweenDateTimeInitialAndDateTimeFinal;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class WebFluxDeviceHandlerTest {

    @Mock
    private GetDevicesUseCase getDevicesUseCase;

    @Mock
    private GetDevicesByTypeUseCase getDevicesByTypeUseCase;

    @Mock
    private CreateKafkaListenersUseCase createKafkaListenersUseCase;

    @Mock
    private GetRegistriesBetweenDateTimeInitialAndDateTimeFinal getRegistriesBetweenDateTimeInitialAndDateTimeFinal;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private WebFluxDeviceHandler handler;

    @Test
    public void shouldReturnDevicesSuccessfully() {
        List<DeviceTypeResponse> deviceTypeResponses = List.of(new DeviceTypeResponse("Type1"),
                new DeviceTypeResponse("Type2"));
        when(getDevicesUseCase.apply()).thenReturn(Mono.just(deviceTypeResponses));

        Mono<ServerResponse> result = handler.getDevices(serverRequest);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    public void shouldReturnDevicesByTypeSuccessfully() {
        when(serverRequest.pathVariable("type")).thenReturn("Type1");
        List<DeviceResponse> deviceResponses = List.of(new DeviceResponse("UUID1", "Device1"),
                new DeviceResponse("UUID2", "Device2"));
        when(getDevicesByTypeUseCase.apply(eq("Type1"))).thenReturn(Mono.just(deviceResponses));

        Mono<ServerResponse> result = handler.getDevicesByType(serverRequest);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    public void shouldReturnStreamDataSuccessfully() {
        Flux<String> messages = Flux.just("Message1", "Message2");
        when(createKafkaListenersUseCase.getMessages()).thenReturn(messages);

        Mono<ServerResponse> result = handler.getStreamData(serverRequest);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    public void shouldReturnStreamDataByTopicSuccessfully() throws NotFoundException {
        when(serverRequest.pathVariable("uuid")).thenReturn("Topic1");
        Flux<String> messages = Flux.just("Message1", "Message2");
        when(createKafkaListenersUseCase.getMessages(eq("Topic1"))).thenReturn(messages);

        Mono<ServerResponse> result = handler.getStreamDataByTopic(serverRequest);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    public void shouldReturnRegistriesBetweenDateTimeSuccessfully() {
        when(serverRequest.pathVariable("uuid")).thenReturn("UUID1");
        when(serverRequest.queryParam("dateTimeInitial")).thenReturn(Optional.of("2025-01-01T12:00:00"));
        when(serverRequest.queryParam("dateTimeFinal")).thenReturn(Optional.of("2025-01-01T12:00:10"));

        List<RegistryResponse> registryResponses = List.of(new RegistryResponse("UUID1", "Data1", "dateTime"),
                new RegistryResponse("UUID2", "Data2", "dateTime"));
        when(getRegistriesBetweenDateTimeInitialAndDateTimeFinal.apply(eq("UUID1"), eq("2025-01-01T12:00:00"),
                eq("2025-01-01T12:00:10")))
                .thenReturn(Mono.just(registryResponses));

        Mono<ServerResponse> result = handler.getRegistriesBetweenDateTimeInitialAndDateTimeFinal(serverRequest);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    public void shouldHandleInvalidRegistriesRequest() {
        when(serverRequest.pathVariable("uuid")).thenReturn("UUID1");
        when(serverRequest.queryParam("dateTimeInitial")).thenReturn(Optional.empty());
        when(serverRequest.queryParam("dateTimeFinal")).thenReturn(Optional.empty());

        Mono<ServerResponse> result = handler.getRegistriesBetweenDateTimeInitialAndDateTimeFinal(serverRequest);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BadRequestException &&
                        throwable.getMessage().contains("This request needs the parameters"))
                .verify();
    }

    @Test
    public void shouldHandleBlankUuid() {
        when(serverRequest.pathVariable("uuid")).thenReturn(" ");
        when(serverRequest.queryParam("dateTimeInitial")).thenReturn(Optional.of("2025-01-01T12:00:00"));
        when(serverRequest.queryParam("dateTimeFinal")).thenReturn(Optional.of("2025-01-01T12:00:10"));

        Mono<ServerResponse> result = handler.getRegistriesBetweenDateTimeInitialAndDateTimeFinal(serverRequest);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BadRequestException &&
                        throwable.getMessage().contains("This request needs the parameters"))
                .verify();
    }

    @Test
    public void shouldHandleMissingDateTimeFinal() {
        when(serverRequest.pathVariable("uuid")).thenReturn("UUID1");
        when(serverRequest.queryParam("dateTimeInitial")).thenReturn(Optional.of("2025-01-01T12:00:00"));
        when(serverRequest.queryParam("dateTimeFinal")).thenReturn(Optional.empty());

        Mono<ServerResponse> result = handler.getRegistriesBetweenDateTimeInitialAndDateTimeFinal(serverRequest);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BadRequestException &&
                        throwable.getMessage().contains("This request needs the parameters"))
                .verify();
    }

}
