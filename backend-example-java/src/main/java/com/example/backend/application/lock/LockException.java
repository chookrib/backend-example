package com.example.backend.application.lock;

import com.example.backend.utility.AppException;

/**
 * Lock Exception
 */
public class LockException extends AppException {

    public LockException() {
        super();
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockException(Throwable cause) {
        super(cause);
    }
}
