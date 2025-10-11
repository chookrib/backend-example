package com.example.ddd.application.lock;

import java.util.function.Supplier;

/**
 * 锁 Service 接口
 */
public interface LockService {

    /**
     * 使用锁执行一个带返回值的操作
     */
    <T> T executeWithLock(String lockKey, Supplier<T> action);

    /**
     * 使用锁执行一个不带返回值的操作
     */
    void executeWithLock(String lockKey, Runnable action);
}
