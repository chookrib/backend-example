package com.example.ddd.domain;

/**
 * Domain异常
 */
public class DomainException extends RuntimeException {
    public DomainException() {
        super();
    }

    public DomainException(String message) {
        super(message);
    }
}