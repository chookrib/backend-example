package com.example.backend.utility;

/**
 * Utility Exception
 */
public class UtilityException extends RuntimeException {

    public UtilityException() { super(); }

    public UtilityException(String message) {
        super(message);
    }

    public UtilityException(String message, Throwable cause) { super(message, cause); }

    public UtilityException(Throwable cause) { super(cause); }
}