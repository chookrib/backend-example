package com.example.backend.adapter.driving;

import com.example.backend.utility.AppException;

/**
 * 未登录 Exception
 */
public class NotLoginException extends AppException {

    public NotLoginException() { super(); }

    public NotLoginException(String message) { super(message); }

    public NotLoginException(String message, Throwable cause) { super(message, cause); }

    public NotLoginException(Throwable cause) { super(cause); }
}

