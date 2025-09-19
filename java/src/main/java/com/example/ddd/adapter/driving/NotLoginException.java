package com.example.ddd.adapter.driving;

/**
 * 未登录异常
 */
public class NotLoginException extends RuntimeException {
    public NotLoginException() {
        super();
    }

    public NotLoginException(String message) {
        super(message);
    }
}

