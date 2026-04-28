package com.example.backend.application.lock;

import com.example.backend.application.ApplicationConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockServiceConfig {

    @Bean
    @ConditionalOnProperty(
            name = "app.lock-service",
            havingValue = "reentrant",
            matchIfMissing = true
    )
    public LockService reentrantLockService(ApplicationConfig applicationConfig) {
        return new ReentrantLockService(
                applicationConfig.isAppEnvDev()
        );
    }

    //==================================================================================================================

    @Value("${app.lock-redisson-address:}")
    private String redissonAddress;

    @Value("${app.lock-redisson-password:}")
    private String redissonPassword;

    @Value("${app.lock-redisson-database:0}")
    private int redissonDatabase;

    @Bean
    @ConditionalOnProperty(
            name = "app.lock-service",
            havingValue = "redisson"
    )
    public LockService redissonLockService(ApplicationConfig applicationConfig) {
        return new RedissonLockService(
                applicationConfig.getAppName(),
                applicationConfig.isAppEnvDev(),
                this.redissonAddress,
                this.redissonPassword,
                this.redissonDatabase
        );
    }
}
