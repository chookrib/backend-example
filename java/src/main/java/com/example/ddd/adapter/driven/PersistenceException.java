package com.example.ddd.adapter.driven;

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