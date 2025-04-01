package com.renz.healthmonitoring.consumerapi.configuration.webflux.exception;

public class UnprocessableEntityException extends ResponseException {

    public UnprocessableEntityException(String message) {
        super("Invalid parameters", 422, message);
    }
    
}
