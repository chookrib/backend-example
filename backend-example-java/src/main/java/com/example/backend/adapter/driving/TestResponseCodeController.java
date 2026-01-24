package com.example.backend.adapter.driving;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 测试响应状态码 Controller
 */
@RestController
public class TestResponseCodeController {

    private static final Logger logger = LoggerFactory.getLogger(TestResponseCodeController.class);

    ///**
    // * 测试响应 500
    // */
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //@RequestMapping(
    //        value = "/api/test/response/code/500",
    //        method = RequestMethod.GET,
    //        produces = "text/plain;charset=UTF-8"
    //)
    //public String testResponseCode500()
    //{
    //    return "测试500错误";
    //}

    /**
     * 测试响应 500
     */
    @RequestMapping(
            value = "/api/test/response/code/500",
            method = RequestMethod.GET,
            produces = "text/plain;charset=UTF-8"
    )
    public String testResponseCode500(HttpServletResponse response)
    {
        response.setStatus(500);
        return "测试500错误";
    }
}
