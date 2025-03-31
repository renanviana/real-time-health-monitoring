package com.renz.healthmonitoring.consumerapi.configuration.webflux;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.renz.healthmonitoring.consumerapi.adapter.DeviceHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@DependsOn("deviceHandler")
@RequiredArgsConstructor
public class RestRouterConfig {

    @RouterOperations({
            @RouterOperation(path = "/devices", method = RequestMethod.GET, operation = @Operation(operationId = "getAllDevices", summary = "Get all devices", tags = {
                    "Device" }, responses = {
                            @ApiResponse(responseCode = "200", description = "OK"),
                            @ApiResponse(responseCode = "404", description = "Devices does not exist")
                    })),
            @RouterOperation(path = "/devices/{type}", method = RequestMethod.GET, operation = @Operation(operationId = "getDevicesByType", summary = "Get device by type", tags = {
                    "Device" }, parameters = {
                            @Parameter(name = "type", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", example = "type"))
                    }, responses = {
                            @ApiResponse(responseCode = "200", description = "OK"),
                            @ApiResponse(responseCode = "404", description = "Device not found")
                    })),
            @RouterOperation(path = "/stream", method = RequestMethod.GET, operation = @Operation(operationId = "getStreamData", summary = "Get all devices data in real time", tags = {
                    "Stream" }, responses = {
                            @ApiResponse(responseCode = "200", description = "OK")
                    })),
            @RouterOperation(path = "/stream/{uuid}", method = RequestMethod.GET, operation = @Operation(operationId = "getStreamByUuid", summary = "Get device data by UUID in real time", tags = {
                    "Stream" }, parameters = {
                            @Parameter(name = "uuid", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", format = "uuid"))
                    }, responses = {
                            @ApiResponse(responseCode = "200", description = "OK")
                    })),
            @RouterOperation(path = "/registry/{uuid}", method = RequestMethod.GET, operation = @Operation(operationId = "getRegistries", summary = "Get device data between datetime initial and datatime final", tags = {
                    "Registry" }, parameters = {
                            @Parameter(name = "uuid", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", format = "uuid")),
                            @Parameter(name = "dateTimeInitial", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "string", format = "date-time")),
                            @Parameter(name = "dateTimeFinal", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "string", format = "date-time"))
                    }, responses = {
                            @ApiResponse(responseCode = "200", description = "OK"),
                            @ApiResponse(responseCode = "400", description = "This request needs the parameters: 'dateTimeInitial' and 'dateTimeInitial'"),
                            @ApiResponse(responseCode = "404", description = "Device does not exist"),
                            @ApiResponse(responseCode = "404", description = "Registries not found for the parameters: 'dateTimeInitial' and 'dateTimeFinal'"),
                    }))
    })
    @Bean
    public RouterFunction<ServerResponse> deviceRoutes(DeviceHandler handler) {
        return RouterFunctions.route()
                .GET("/devices", handler::getDevices)
                .GET("/devices/{type}", handler::getDevicesByType)
                .GET("/stream", handler::getStreamData)
                .GET("/stream/{uuid}", handler::getStreamDataByTopic)
                .GET("/registry/{uuid}", handler::getRegistriesBetweenDateTimeInitialAndDateTimeFinal)
                .build();
    }

}