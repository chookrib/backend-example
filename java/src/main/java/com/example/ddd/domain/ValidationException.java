package com.example.ddd.domain;

/**
 * 验证异常
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
