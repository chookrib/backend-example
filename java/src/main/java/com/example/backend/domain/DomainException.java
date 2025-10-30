package com.example.backend.domain;

/**
 * Domain Exception
 */
public class DomainException extends RuntimeException {

    public DomainException() { super(); }

    public DomainException(String message) {
        super(message);
    }
}