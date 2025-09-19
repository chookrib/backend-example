package com.example.ddd.adapter.driving;

import com.example.ddd.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Well Known Controller
 */
@Controller
public class WellKnownController {
    private static final Logger logger = LoggerFactory.getLogger(WellKnownController.class);

    /**
     * 应用信息
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
     * 测试异常
     */
    @RequestMapping(value = "/.well-known/exception", method = RequestMethod.GET)
    @ResponseBody
    public Result exception() {
        throw new RuntimeException("测试异常");
    }

}


