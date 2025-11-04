package com.example.backend.application.lock;

import com.example.backend.application.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 测试锁 Service
 */
@Component
public class TestLockService {

    private static final Logger logger = LoggerFactory.getLogger(TestLockService.class);

    private final LockService lockService;

    public TestLockService(LockService lockService) {
        this.lockService = lockService;
    }

    private int count = 10;

    /**
     * 设置 count
     */
    public void setCount(int value) {
        this.count = value;
        logger.info("设置 count = {}", value);
    }

    /**
     * 减少 count，不加锁
     */
    public boolean decreaseCount() {
        int c = this.count;
        if (c > 0) {
            this.count--;
            logger.info("减少 count 成功: {} - 1 = {}", c, this.count);
            return true;
        } else {
            logger.info("减少 count 失败: {}", c);
            return false;
        }
    }

    /**
     * 减少 count，加锁
     */
    public void decreaseCountWithLock() {
        try (AutoCloseable lock = this.lockService.lock(LockKeys.TEST)) {
            this.decreaseCount();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //==============================================================================================================

    /**
     * Thread.Sleep
     */
    public void threadSleep() {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException ex) {
            throw new ApplicationException(ex);
        }
    }
}
