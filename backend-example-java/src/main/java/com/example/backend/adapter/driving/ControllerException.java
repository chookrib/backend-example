package com.example.backend.adapter.driving;

import com.example.backend.utility.AppException;

/**
 * Controller Exception
 */
public class ControllerException extends AppException {

    public ControllerException() { super(); }

    public ControllerException(String message) {
        super(message);
    }

    public ControllerException(String message, Throwable cause) { super(message, cause); }

    public ControllerException(Throwable cause) { super(cause); }
}