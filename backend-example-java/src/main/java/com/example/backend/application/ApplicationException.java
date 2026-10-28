package com.example.backend.application;

import com.example.backend.utility.AppException;

/**
 * Application Exception
 */
public class ApplicationException extends AppException {

    public ApplicationException() { super(); }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) { super(message, cause); }

    public ApplicationException(Throwable cause) { super(cause); }
}