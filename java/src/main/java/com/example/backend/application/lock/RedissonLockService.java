package com.example.backend.application.lock;

import com.example.backend.Accessor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 基于 Redisson 实现的分布式锁 Service
 */
public class RedissonLockService implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(RedissonLockService.class);

    private final RedissonClient redissonClient;

    //public RedissonLockService(RedissonClient redissonClient) {
    public RedissonLockService(String address, String password, int database) {
        //this.redissonClient = redissonClient;
        Config config = new Config();
        config.useSingleServer()
                .setAddress(address)    // redis://127.0.0.1:6379
                .setPassword(password)
                .setDatabase(database);
        this.redissonClient = org.redisson.Redisson.create(config);
    }

    @Override
    public <T> T getWithLock(String key, Supplier<T> action) {
        RLock lock = this.redissonClient.getLock(key);
        lock.lock(); // Redisson 会自动续期
        if(Accessor.isDevelopment)
            logger.info("线程 {} 获取 Redisson 锁 {} 成功", Thread.currentThread().getName(), key);
        try {
            return action.get();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                if(Accessor.isDevelopment)
                    logger.info("线程 {} 释放 Redisson 锁 {} 成功", Thread.currentThread().getName(), key);
            }
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