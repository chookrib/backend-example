package com.example.backend.adapter.driven;

import com.example.backend.utility.AppException;

/**
 * Gateway Exception
 */
public class GatewayException extends AppException {

    public GatewayException() {
        super();
    }

    public GatewayException(String message) { super(message); }

    public GatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayException(Throwable cause) {
        super(cause);
    }
}