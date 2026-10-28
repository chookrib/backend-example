package com.example.backend.adapter.driven;

import com.example.backend.utility.AppException;

/**
 * Persistence Exception
 */
public class PersistenceException extends AppException {

    public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) { super(cause); }
}