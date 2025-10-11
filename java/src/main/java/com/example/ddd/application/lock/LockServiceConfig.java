package com.example.ddd.application.lock;

import org.redisson.api.RedissonClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockServiceConfig {

    @Bean
    @ConditionalOnProperty(
            name = "app.lock-service",
            havingValue = "synchronized",
            matchIfMissing = true)
    public LockService synchronizedLockService() {
        return new SynchronizedLockService();
    }

    @Bean
    @ConditionalOnProperty(name = "app.lock-service", havingValue = "reentrant")
    public LockService reentrantLockService() {
        return new ReentrantLockService();
    }

    @Bean
    @ConditionalOnProperty(name = "app.lock-service", havingValue = "redisson")
    public LockService redissonLockService(RedissonClient redissonClient) {
        return new RedissonLockService(redissonClient);
    }
}
