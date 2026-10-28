package com.example.backend.domain;

import com.example.backend.utility.AppException;

/**
 * Domain Exception
 */
public class DomainException extends AppException {

    public DomainException() { super(); }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) { super(message, cause); }

    public DomainException(Throwable cause) { super(cause); }
}