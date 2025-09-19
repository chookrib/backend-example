package com.example.ddd.adapter.driven;

/**
 * Repository异常
 */
public class RepositoryException extends RuntimeException {
    public RepositoryException() {
        super();
    }

    public RepositoryException(String message) {
        super(message);
    }
}