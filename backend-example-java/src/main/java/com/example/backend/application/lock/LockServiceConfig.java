package com.example.backend.application.lock;

import com.example.backend.application.ApplicationConfig;
import com.example.backend.application.ApplicationException;
import com.example.backend.utility.ValueUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContextException;
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

    @Value("${app.lock-redisson-key-prefix:}")
    private String redissonKeyPrefix;

    @Bean
    @ConditionalOnProperty(
            name = "app.lock-service",
            havingValue = "redisson"
    )
    public LockService redissonLockService(ApplicationConfig applicationConfig) {
        if(ValueUtility.isEmptyString(redissonAddress))
            throw new ApplicationException("未配置 app.lock-redisson-address");
        if(ValueUtility.isEmptyString(redissonKeyPrefix))
            throw new ApplicationException("未配置 app.lock-redisson-key-prefix");
        return new RedissonLockService(
                redissonKeyPrefix,
                applicationConfig.isAppEnvDev(),
                redissonAddress,
                redissonPassword,
                redissonDatabase
        );
    }
}
