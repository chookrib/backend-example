package com.example.ddd.adapter.driving;

import com.example.ddd.application.lock.TestLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试锁 Well Known Controller
 */
@RestController
public class WellKnownTestLockController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownTestLockController.class);

    private final TestLockService testLockService;

    public WellKnownTestLockController(TestLockService testLockService) {
        this.testLockService = testLockService;
    }

    /**
     * 扣减测试，不加锁
     */
    @RequestMapping(value = "/.well-known/test/lock/reduce-unsafe", method = RequestMethod.GET)
    public Result testLockReduceUnsafe() {
        this.testLockService.reduceUnsafe();
        return Result.ok();
    }

    /**
     * 扣减测试，加锁
     */
    @RequestMapping(value = "/.well-known/test/lock/reduce-safe", method = RequestMethod.GET)
    public Result testLockReduceSafe() {
        this.testLockService.reduceSafe();
        return Result.ok();
    }

    /**
     * 加锁等待测试
     */
    @RequestMapping(value = "/.well-known/test/lock/sleep", method = RequestMethod.GET)
    public Result testLockSleep() {
        this.testLockService.lockSleep();
        return Result.ok();
    }
}


