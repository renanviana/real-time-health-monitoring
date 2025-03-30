package com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.handler;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.renz.healthmonitoring.consumerapi.configuration.webflux.exception.ResponseException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResponseException.class)
        public Mono<ResponseEntity<Map<String, Object>>> handleResourceNotFound(ResponseException e) {
                return Mono.just(ResponseEntity.status(HttpStatus.valueOf(e.getStatus()))
                                .body(Map.of(
                                                "timestamp", LocalDateTime.now(),
                                                "status", e.getStatus(),
                                                "error", e.getError(),
                                                "message", e.getMessage())));
        }

}
