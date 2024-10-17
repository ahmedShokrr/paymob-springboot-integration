package com.example.paymob_springboot_integration.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {


    private HttpStatus status;
    private String message;

    public CustomException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
