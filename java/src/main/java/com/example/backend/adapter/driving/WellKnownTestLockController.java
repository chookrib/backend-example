package com.example.backend.adapter.driving;

import com.example.backend.application.lock.TestLockService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
     * 设置 count
     */
    @RequestMapping(value = "/.well-known/test/lock/set-count", method = RequestMethod.GET)
    public Result testLockSetCount(HttpServletRequest request) {
        int value = RequestValueHelper.getRequestParamInt(request, 1, "value");
        this.testLockService.setCount(value);
        return Result.okData(Map.of("count", value));
    }

    /**
     * 减少 count，不加锁
     */
    @RequestMapping(value = "/.well-known/test/lock/decrease-count", method = RequestMethod.GET)
    public Result testLockDecreaseCount() {
        this.testLockService.decreaseCount();
        return Result.ok();
    }

    /**
     * 减少 count，加锁
     */
    @RequestMapping(value = "/.well-known/test/lock/decrease-count-with-lock", method = RequestMethod.GET)
    public Result testLockDecreaseCountWithLock() {
        this.testLockService.decreaseCountWithLock();
        return Result.ok();
    }

    //==================================================================================================================

    /**
     * Thread.Sleep
     */
    @RequestMapping(value = "/.well-known/test/lock/thread-sleep", method = RequestMethod.GET)
    public Result testLockThreadSleep() {
        this.testLockService.threadSleep();
        return Result.ok();
    }
}


