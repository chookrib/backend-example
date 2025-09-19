package com.example.ddd.adapter.driving;

/**
 * Controller异常
 */
public class ControllerException extends RuntimeException {
    public ControllerException() {
        super();
    }

    public ControllerException(String message) {
        super(message);
    }
}