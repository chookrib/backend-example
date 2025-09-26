package com.example.ddd.adapter.driven;

/**
 * Query Exception
 */
public class QueryException extends RuntimeException {

    public QueryException() {
        super();
    }

    public QueryException(String message) {
        super(message);
    }
}