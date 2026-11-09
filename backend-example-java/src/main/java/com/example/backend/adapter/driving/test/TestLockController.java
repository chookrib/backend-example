package com.example.backend.adapter.driving.test;

import com.example.backend.adapter.driving.Result;
import com.example.backend.application.lock.LockException;
import com.example.backend.application.test.TestLockService;
import com.example.backend.utility.ValueUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 测试锁 Controller
 */
@RestController
public class TestLockController {

    private static final Logger logger = LoggerFactory.getLogger(TestLockController.class);

    private final TestLockService testLockService;

    public TestLockController(TestLockService testLockService) {
        this.testLockService = testLockService;
    }

    /**
     * 设置 count
     */
    @RequestMapping(value = "/api/test/lock/set-count", method = RequestMethod.GET)
    public Result testLockSetCount(HttpServletRequest request) {
        int value = ValueUtility.defaultIfNull(
                ValueUtility.toIntOrNull(request.getParameter("value")),
                1);
        testLockService.setCount(value);
        return Result.okData(Map.of("count", value));
    }

    /**
     * 减少 count，不加锁
     */
    @RequestMapping(value = "/api/test/lock/decrease-count", method = RequestMethod.GET)
    public Result testLockDecreaseCount() {
        testLockService.decreaseCount();
        return Result.ok();
    }

    /**
     * 减少 count，加锁
     */
    @RequestMapping(value = "/api/test/lock/decrease-count-with-lock", method = RequestMethod.GET)
    public Result testLockDecreaseCountWithLock() {
        testLockService.decreaseCountWithLock();
        return Result.ok();
    }

    // =================================================================================================================

    /**
     * Thread.Sleep
     */
    @RequestMapping(value = "/api/test/lock/thread-sleep", method = RequestMethod.GET)
    public Result testLockThreadSleep() {
        testLockService.threadSleep();
        return Result.ok();
    }

    // =================================================================================================================

    /**
     * 测试 LockException 异常
     */
    @RequestMapping(value = "/api/test/lock/exception", method = RequestMethod.GET)
    public Result testLockException() {
        throw new LockException("测试 LockException 异常");
    }
}


