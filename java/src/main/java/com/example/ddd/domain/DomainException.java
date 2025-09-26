package com.example.ddd.domain;

/**
 * Domain Exception
 */
public class DomainException extends RuntimeException {

    public DomainException() { super(); }

    public DomainException(String message) {
        super(message);
    }
}