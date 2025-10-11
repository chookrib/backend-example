package com.example.ddd.application.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
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
    public <T> T executeWithLock(String lockKey, Supplier<T> action) {
        ReentrantLock lock = lockMap.computeIfAbsent(lockKey, k -> new ReentrantLock());
        lock.lock();
        //logger.info("线程 [{}] 获得 ReentrantLock 锁: {}", Thread.currentThread().getName(), lockKey);
        try {
            return action.get();
        } finally {
            lock.unlock();
            //logger.info("线程 [{}] 释放 ReentrantLock 锁: {}", Thread.currentThread().getName(), lockKey);
        }
    }

    @Override
    public void executeWithLock(String lockKey, Runnable action) {
        executeWithLock(lockKey, () -> {
            action.run();
            return null;
        });
    }

    // ReentrantReadWriteLock
    //public static final ReentrantReadWriteLock REENTRANT_READ_WRITE_LOCK = new ReentrantReadWriteLock();
    //public final Lock REENTRANT_READ_LOCK = REENTRANT_READ_WRITE_LOCK.readLock();
    //public final Lock REENTRANT_WRITE_LOCK = REENTRANT_READ_WRITE_LOCK.writeLock();
    //private int count;
    //
    //public int readLock() {
    //    REENTRANT_READ_LOCK.lock(); // 多个读线程可以同时进入
    //    try {
    //        return count;
    //    } finally {
    //        READ_LOCK.unlock();
    //    }
    //}
    //
    //public void writeLock() {
    //    REENTRANT_WRITE_LOCK.lock(); // 写操作互斥
    //    try {
    //        count++;
    //    } finally {
    //        WRITE_LOCK.unlock();
    //    }
    //}
}