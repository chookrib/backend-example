package com.example.backend.application.lock;

import com.example.backend.Accessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 基于 ReentrantLock 实现的锁 Service
 */
public class ReentrantLockService implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(ReentrantLockService.class);

    // 存储锁对象的Map，Key为锁标识，Value为ReentrantLock实例
    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public <T> T getWithLock(String key, Supplier<T> action) {
        ReentrantLock lock = this.lockMap.computeIfAbsent(key, k -> new ReentrantLock());

        // 不等待获取锁
        //lock.lock();
        //if (Accessor.appIsDev)
        //    logger.info("线程 {} 获取 ReentrantLock 锁 {} 成功", Thread.currentThread().getName(), key);
        //try {
        //    return action.get();
        //} finally {
        //    lock.unlock();
        //    if (Accessor.appIsDev)
        //        logger.info("线程 {} 释放 ReentrantLock 锁 {} 成功", Thread.currentThread().getName(), key);
        //}

        // 等待一定时间获取锁
        try {
            if (!lock.tryLock(10, TimeUnit.SECONDS)) {
                if (Accessor.appIsDev)
                    logger.info("线程 {} 获取 ReentrantLock 锁 {} 成功", Thread.currentThread().getName(), key);
                try {
                    return action.get();
                } finally {
                    lock.unlock();
                    if (Accessor.appIsDev)
                        logger.info("线程 {} 释放 ReentrantLock 锁 {} 成功", Thread.currentThread().getName(), key);
                }
            } else {
                throw new LockException(String.format("获取 Redisson 锁 %s 失败", key));
            }
        } catch (InterruptedException ex) {
            throw new LockException(String.format("获取 Redisson 锁 %s 失败: %s", key, ex.getMessage()), ex);
        }
    }

    @Override
    public void runWithLock(String key, Runnable action) {
        getWithLock(key, () -> {
            action.run();
            return null;
        });
    }
}