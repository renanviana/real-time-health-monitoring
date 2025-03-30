package com.renz.healthmonitoring.consumerapi.configuration.webflux.exception;

import lombok.Getter;

@Getter
public class ResponseException extends RuntimeException {
    
    private String error;
    private Integer status;

    public ResponseException(String error, Integer status, String message) {
        super(message);
        this.error = error;
        this.status = status;
    }

}
