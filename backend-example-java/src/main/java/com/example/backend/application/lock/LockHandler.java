package com.example.backend.application.lock;

/**
 * 锁句柄，使用 try-with-resources 语法自动释放锁
 */
public interface LockHandler extends AutoCloseable {

    @Override
    void close();
}
