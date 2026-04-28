package com.example.backend.application.lock;

import com.example.backend.application.ApplicationConfig;
import com.example.backend.application.ApplicationException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redisson 实现的锁 Service
 */
public class RedissonLockService implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(RedissonLockService.class);

    private final String lockKeyPrefix;
    private final boolean enableLog;
    private final RedissonClient redissonClient;

    //public RedissonLockService(RedissonClient redissonClient) {
    public RedissonLockService(String lockKeyPrefix, boolean enableLog, String address, String password, int database) {
        //this.redissonClient = redissonClient;
        this.lockKeyPrefix = lockKeyPrefix;
        this.enableLog = enableLog;
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
    public AutoCloseable lock(String key) {
        String lockKey = lockKeyPrefix + ":lock:" + key;
        RLock lock = this.redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                if (enableLog) {
                    logger.info("线程 {} 获取 Redisson 锁 {} 成功", Thread.currentThread().getName(), key);
                }
                return new RedissonLockHandler(enableLog, lock, key);
            } else {
                throw new LockException(String.format("获取 Redisson 锁 %s 失败", key));
            }
        } catch (InterruptedException ex) {
            throw new LockException(String.format("获取 Redisson 锁 %s 失败: %s", key, ex.getMessage()), ex);
        }
    }

    /**
     * RedissonLock 锁处理器
     */
    private static class RedissonLockHandler implements AutoCloseable {

        private final boolean enableLog;
        private final RLock lock;
        private final String lockKey;

        public RedissonLockHandler(boolean enableLog, RLock lock, String lockKey) {
            this.enableLog = enableLog;
            this.lock = lock;
            this.lockKey = lockKey;
        }

        @Override
        public void close() {
            if (this.lock.isLocked() && this.lock.isHeldByCurrentThread()) {
                this.lock.unlock();
                if (enableLog) {
                    logger.info("线程 {} 释放 Redisson 锁 {} 成功", Thread.currentThread().getName(), lockKey);
                }
            }
        }
    }
}