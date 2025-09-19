package com.example.ddd.domain;

/**
 * Domain层异常
 */
public class DomainException extends RuntimeException {
    public DomainException() {
        super();
    }

    public DomainException(String message) {
        super(message);
    }
}