package com.example.ddd.adapter.driven;

/**
 * Gateway异常
 */
public class GatewayException extends RuntimeException {
    public GatewayException() {
        super();
    }

    public GatewayException(String message) {
        super(message);
    }
}