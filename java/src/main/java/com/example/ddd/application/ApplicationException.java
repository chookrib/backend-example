package com.example.ddd.application;

/**
 * Application异常
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }
}