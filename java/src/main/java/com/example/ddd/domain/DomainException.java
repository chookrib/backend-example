package com.example.ddd.domain;

/**
 * 领域层异常
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}