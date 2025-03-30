package com.renz.healthmonitoring.consumerapi.configuration.webflux.exception;

public class BadRequestException extends ResponseException {

    public BadRequestException(String message) {
        super("Bad Request", 400, message);
    }

}
