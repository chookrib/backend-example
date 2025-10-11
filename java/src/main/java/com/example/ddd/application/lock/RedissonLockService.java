package com.example.ddd.application.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 基于 Redisson 实现的分布式锁 Service
 */
public class RedissonLockService implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(RedissonLockService.class);

    private final RedissonClient redissonClient;

    public RedissonLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public <T> T executeWithLock(String lockKey, Supplier<T> action) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(); // Redisson 会自动续期
        //logger.info("线程 [{}] 获得 Redisson 锁: {}", Thread.currentThread().getName(), lockKey);
        try {
            return action.get();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                //logger.info("线程 [{}] 释放 Redisson 锁: {}", Thread.currentThread().getName(), lockKey);
            }
        }
    }

    @Override
    public void executeWithLock(String lockKey, Runnable action) {
        executeWithLock(lockKey, () -> {
            action.run();
            return null;
        });
    }
}