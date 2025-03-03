package com.renz.healthmonitoring.consumerapi.adapter.webflux;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceHandler;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceResponse;
import com.renz.healthmonitoring.consumerapi.domain.response.webflux.DeviceTypeResponse;
import com.renz.healthmonitoring.consumerapi.usecases.GetDeviceTypesUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetDevicesByNameUseCase;
import com.renz.healthmonitoring.consumerapi.usecases.GetRegistersByDeviceNameAndDeviceIdUseCase;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebFluxDeviceHandler implements DeviceHandler {

    private final GetDeviceTypesUseCase getDeviceTypesUseCase;
    private final GetDevicesByNameUseCase getDevicesByNameUseCase;
    private final GetRegistersByDeviceNameAndDeviceIdUseCase getRegistersByDeviceNameAndDeviceIdUseCase;

    public WebFluxDeviceHandler(
            GetDeviceTypesUseCase getDeviceTypesUseCase,
            GetDevicesByNameUseCase getDevicesByNameUseCase,
            GetRegistersByDeviceNameAndDeviceIdUseCase getRegistersByDeviceNameAndDeviceIdUseCase) {
        this.getDeviceTypesUseCase = getDeviceTypesUseCase;
        this.getDevicesByNameUseCase = getDevicesByNameUseCase;
        this.getRegistersByDeviceNameAndDeviceIdUseCase = getRegistersByDeviceNameAndDeviceIdUseCase;
    }

    @Override
    public Mono<ServerResponse> getDeviceTypes(ServerRequest request) {
        Mono<List<DeviceTypeResponse>> results = getDeviceTypesUseCase.apply();
        return results.flatMap(items -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(items)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Override
    public Mono<ServerResponse> getDevicesByName(ServerRequest request) {
        String deviceName = request.pathVariable("deviceName");
        Mono<List<DeviceResponse>> results = getDevicesByNameUseCase.apply(deviceName);
        return results.flatMap(items -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(items)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Override
    public Mono<ServerResponse> getRegistersByDeviceNameAndDeviceId(ServerRequest request) {
        String deviceName = request.pathVariable("deviceName");
        String deviceId = request.pathVariable("deviceId");
        Flux<String> results = getRegistersByDeviceNameAndDeviceIdUseCase.apply(deviceName, deviceId);
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(results, String.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
