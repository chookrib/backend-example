package com.example.ddd.adapter.driven;

/**
 * QueryHandler异常
 */
public class QueryException extends RuntimeException {
    public QueryException() {
        super();
    }

    public QueryException(String message) {
        super(message);
    }
}