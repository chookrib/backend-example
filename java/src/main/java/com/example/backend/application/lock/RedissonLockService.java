package com.example.backend.application.lock;

import com.example.backend.Accessor;
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
    public AutoCloseable lock(String key) {
        String lockKey = Accessor.appName + ":lock:" + key;
        RLock lock = this.redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                if (Accessor.appIsDev)
                    logger.info("线程 {} 获取 Redisson 锁 {} 成功", Thread.currentThread().getName(), key);
                return new RedissonLockHandler(lock, key);
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

        private final RLock lock;
        private final String lockKey;

        public RedissonLockHandler(RLock lock, String lockKey) {
            this.lock = lock  ;
            this.lockKey = lockKey;
        }

        @Override
        public void close() {
            if (this.lock.isLocked() && this.lock.isHeldByCurrentThread()) {
                this.lock.unlock();
                if (Accessor.appIsDev)
                    logger.info("线程 {} 释放 Redisson 锁 {} 成功", Thread.currentThread().getName(), lockKey);
            }
        }
    }
}