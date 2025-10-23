package com.example.ddd.adapter.driving;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试异常 Well Known Controller
 */
@RestController
public class WellKnownTestExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownTestExceptionController.class);

    /**
     * 测试异常处理
     */
    @RequestMapping(value = "/.well-known/test/exception", method = RequestMethod.GET)
    public Result testException() {
        throw new RuntimeException("测试异常");
    }
}


