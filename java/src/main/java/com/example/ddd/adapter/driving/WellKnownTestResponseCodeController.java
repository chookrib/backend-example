package com.example.ddd.adapter.driving;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 测试响应 Well Known Controller
 */
@RestController
public class WellKnownTestResponseCodeController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownTestResponseCodeController.class);

    ///**
    // * 500
    // */
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //@RequestMapping(value = "/.well-known/test/response/code/500", method = RequestMethod.GET,
    //        produces = "text/plain;charset=UTF-8")
    //public String testResponseCode500()
    //{
    //    return "测试500错误";
    //}

    /**
     * 500
     */
    @RequestMapping(value = "/.well-known/test/response/code/500", method = RequestMethod.GET,
            produces = "text/plain;charset=UTF-8")
    public String testResponseCode500(HttpServletResponse response)
    {
        response.setStatus(500);
        return "测试500错误";
    }
}
