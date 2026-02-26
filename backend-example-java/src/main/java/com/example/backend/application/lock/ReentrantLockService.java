package com.example.backend.application.lock;

import com.example.backend.Accessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于 ReentrantLock 实现的锁 Service
 */
public class ReentrantLockService implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(ReentrantLockService.class);

    // 存储锁对象的Map，Key为锁标识，Value为ReentrantLock实例
    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public AutoCloseable lock(String key) {
        ReentrantLock lock = this.lockMap.computeIfAbsent(key, k -> new ReentrantLock());
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                if (Accessor.appEnvIsDev)
                    logger.info("线程 {} 获取 ReentrantLock 锁 {} 成功", Thread.currentThread().getName(), key);
                return new ReentrantLockHandler(lock, key);
            } else {
                throw new LockException(String.format("获取 Reentrant 锁 %s 失败", key));
            }
        } catch (InterruptedException ex) {
            throw new LockException(String.format("获取 Reentrant 锁 %s 失败: %s", key, ex.getMessage()), ex);
        }
    }

    /**
     * ReentrantLock 锁处理器
     */
    private static class ReentrantLockHandler implements AutoCloseable {

        private final ReentrantLock reentrantLock;
        private final String key;

        public ReentrantLockHandler(ReentrantLock reentrantLock, String key) {
            this.reentrantLock = reentrantLock;
            this.key = key;
        }

        @Override
        public void close() {
            if(this.reentrantLock.isLocked() && this.reentrantLock.isHeldByCurrentThread()) {
                this.reentrantLock.unlock();
                if (Accessor.appEnvIsDev)
                    logger.info("线程 {} 释放 Reentrant 锁 {} 成功", Thread.currentThread().getName(), key);
            }
        }
    }
}