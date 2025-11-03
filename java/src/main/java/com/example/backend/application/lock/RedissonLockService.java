package com.example.backend.application.lock;

import com.example.backend.Accessor;
import com.example.backend.application.ApplicationException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
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
        try {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress(address)
                    .setPassword(password)
                    .setDatabase(database);
            this.redissonClient = org.redisson.Redisson.create(config);
        } catch (Exception ex) {
            throw new ApplicationException("RedissonLockService 初始化失败", ex);
        }
    }

    @Override
    public <T> T getWithLock(String key, Supplier<T> action) {
        String lockKey = Accessor.appName + ":lock:" + key;
        RLock lock = this.redissonClient.getLock(lockKey);

        // 不等待获取锁
        //lock.lock(); // Redisson 会自动续期
        //if (Accessor.appIsDev)
        //    logger.info("线程 {} 获取 Redisson 锁 {} 成功", Thread.currentThread().getName(), lockKey);
        //try {
        //    return action.get();
        //} finally {
        //    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
        //        lock.unlock();
        //        if (Accessor.appIsDev)
        //            logger.info("线程 {} 释放 Redisson 锁 {} 成功", Thread.currentThread().getName(), lockKey);
        //    }
        //}

        // 等待一定时间获取锁
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                if (Accessor.appIsDev)
                    logger.info("线程 {} 获取 Redisson 锁 {} 成功", Thread.currentThread().getName(), lockKey);
                try {
                    return action.get();
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        if (Accessor.appIsDev)
                            logger.info("线程 {} 释放 Redisson 锁 {} 成功", Thread.currentThread().getName(), lockKey);
                    }
                }
            } else {
                throw new LockException(String.format("获取 Redisson 锁 %s 失败", lockKey));
            }
        } catch (InterruptedException ex) {
            throw new LockException(String.format("获取 Redisson 锁 %s 失败: %s", lockKey, ex.getMessage()), ex);
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