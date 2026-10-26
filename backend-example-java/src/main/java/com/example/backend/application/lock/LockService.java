package com.example.backend.application.lock;

/**
 * 锁 Service 接口
 */
public interface LockService {

    /**
     * 获取一个锁，返回锁句柄，使用 try-with-resources 语法自动释放锁
     */
    LockHandler lock(String key);
}
