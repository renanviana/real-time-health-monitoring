package com.renz.healthmonitoring.consumerapi.configuration.webflux.exception;

public class NotFoundException extends ResponseException {

    public NotFoundException(String message) {
        super("Not found", 404, message);
    }

}
