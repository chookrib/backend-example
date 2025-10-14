package com.example.ddd.application.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 基于 synchronized 关键字实现的锁 Service
 */
public class SynchronizedLockService implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizedLockService.class);

    // 存储锁对象的Map，Key为锁的标识，Value为锁对象
    private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();

    @Override
    public <T> T executeWithLock(String key, Supplier<T> action) {
        Object lock = lockMap.computeIfAbsent(key, k -> new Object());
        synchronized (lock) {
            //logger.info("线程 [{}] 获取 Synchronized 锁成功: {}", Thread.currentThread().getName(), key);
            try {
                return action.get();
            } finally {
                //logger.info("线程 [{}] 释放 Synchronized 锁成功: {}", Thread.currentThread().getName(), key);
                // 不移除锁对象以便复用
            }
        }
    }

    @Override
    public void executeWithLock(String key, Runnable action) {
        executeWithLock(key, () -> {
            action.run();
            return null;
        });
    }
}
