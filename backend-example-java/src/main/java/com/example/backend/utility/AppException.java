package com.example.backend.utility;

/**
 * 应用异常基类
 */
public abstract class AppException extends RuntimeException {

    public AppException() { super(); }

    public AppException(String message) { super(message); }

    public AppException(String message, Throwable cause) { super(message, cause); }

    public AppException(Throwable cause) { super(cause); }
}
