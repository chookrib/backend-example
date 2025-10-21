package com.example.ddd.application.lock;

import com.example.ddd.Application;
import com.example.ddd.application.ApplicationException;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

/**
 * 测试锁 Service
 */
@Component
public class TestLockService {

    private final LockService lockService;

    public TestLockService(LockService lockService) {
        this.lockService = lockService;
    }

    private int count = 10;

    /**
     * 扣减测试，不加锁
     */
    public void reduceUnsafe() {
        if (count > 0) {
            count--;
            System.out.println("扣减后 count = " + count);
        } else {
            System.out.println("无法扣减 count = " + count);
        }
    }

    /**
     * 扣减测试，加锁
     */
    public void reduceSafe() {
        this.lockService.executeWithLock(LockKeys.TEST, () -> {
            this.reduceUnsafe();
        });
    }

    /**
     * 加锁等待测试
     */
    public void lockSleep(){
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
