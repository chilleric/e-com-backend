package com.example.ecom.exception;

import java.util.Map;

public class InvalidRequestException extends RuntimeException {

    protected Map<String, String> result;

    public InvalidRequestException(Map<String, String> input, String message) {
        super(message);
        this.result = input;
    }

    public Map<String, String> getResult() {
        return this.result;
    }
}
