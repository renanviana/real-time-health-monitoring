package com.renz.healthmonitoring.consumerapi.adapter.webflux;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceHandler;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.RegistryResponse;
import com.renz.healthmonitoring.consumerapi.usecases.CreateKafkaListenersUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByTypeUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistriesBetweenDateTimeInitialAndDateTimeFinal;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class WebFluxDeviceHandler implements DeviceHandler {

    private final GetDevicesUseCase getDevicesUseCase;
    private final GetDevicesByTypeUseCase getDevicesByTypeUseCase;
    private final CreateKafkaListenersUseCase createKafkaListenersUseCase;
    private final GetRegistriesBetweenDateTimeInitialAndDateTimeFinal getRegistriesBetweenDateTimeInitialAndDateTimeFinal;

    @Override
    public Mono<ServerResponse> getDevices(ServerRequest request) {
        Mono<List<DeviceTypeResponse>> results = getDevicesUseCase.apply();
        return results.flatMap(items -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(items)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Override
    public Mono<ServerResponse> getDevicesByType(ServerRequest request) {
        String type = request.pathVariable("type");
        Mono<List<DeviceResponse>> results = getDevicesByTypeUseCase.apply(type);
        return results.flatMap(items -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(items)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Override
    public Mono<ServerResponse> getStreamDataDevices(ServerRequest request) {
        Flux<String> results = createKafkaListenersUseCase.getMessages();
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(results, String.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Override
    public Mono<ServerResponse> getStreamDataByTopic(ServerRequest request) {
        String topic = request.pathVariable("id");
        Flux<String> results = createKafkaListenersUseCase.getMessages(topic);
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(results, String.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Override
    public Mono<ServerResponse> getRegistriesBetweenDateTimeInitialAndDateTimeFinal(ServerRequest request) {
        String uuid = request.pathVariable("uuid");
        Optional<String> dateTimeInitial = request.queryParam("dateTimeInitial");
        Optional<String> dateTimeFinal = request.queryParam("dateTimeFinal");
        if (isNotBlank(uuid) && dateTimeInitial.isPresent() && dateTimeFinal.isPresent()) {
            Mono<List<RegistryResponse>> registries = getRegistriesBetweenDateTimeInitialAndDateTimeFinal.apply(uuid,
            dateTimeInitial.get(), dateTimeFinal.get());
            return registries.flatMap(items -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(items)))
                    .switchIfEmpty(ServerResponse.notFound().build());
        }
        throw new RuntimeException("TODO: ERROR");
    }

}
