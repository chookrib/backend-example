package com.example.backend.adapter.driving;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试异常 Controller
 */
@RestController
public class TestExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(TestExceptionController.class);

    /**
     * 测试异常处理
     */
    @RequestMapping(value = "/api/test/exception", method = RequestMethod.GET)
    public Result testException() {
        throw new RuntimeException("测试异常");
    }
}


