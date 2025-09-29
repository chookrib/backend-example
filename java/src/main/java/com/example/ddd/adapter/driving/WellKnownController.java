package com.example.ddd.adapter.driving;

import com.example.ddd.Application;
import com.example.ddd.utility.JacksonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Well Known Controller
 */
@Controller
public class WellKnownController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownController.class);

    /**
     * 应用信息，显示非涉密信息
     */
    @RequestMapping(value = "/.well-known/info", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String info() {
        return "File-Name: " + Application.getFileName() +
               System.lineSeparator() +
               "Build-Time: " + Application.getBuildTime() +
               System.lineSeparator();
    }

    /**
     * 测试异常处理
     */
    @RequestMapping(value = "/.well-known/test-exception", method = RequestMethod.GET)
    @ResponseBody
    public Result testException() {
        throw new RuntimeException("测试异常");
    }

    /**
     * 测试JSON数据输出
     */
    @RequestMapping(value = "/.well-known/test-json", method = RequestMethod.GET)
    @ResponseBody
    public Result testJson() {
        JacksonUtility.TestClass c = new JacksonUtility().new TestClass();
        return Result.okData(c);
    }

    /**
     * 测试JSON数据输出
     */
    @RequestMapping(value = "/.well-known/test-json-ref", method = RequestMethod.GET)
    @ResponseBody
    public Result testJsonRef() {
        JacksonUtility.TestClassRef c = new JacksonUtility().new TestClassRef();
        return Result.okData(c);
    }
}


