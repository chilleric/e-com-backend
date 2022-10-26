package com.example.ecom.exception;

public class BadSqlException extends RuntimeException {
    public BadSqlException(String message) {
        super(message);
    }
}
