package com.example.backend.adapter.driven;

/**
 * Persistence Exception
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);
    }
}