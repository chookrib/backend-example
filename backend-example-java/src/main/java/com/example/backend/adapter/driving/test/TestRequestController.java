package com.example.backend.adapter.driving.test;

import com.example.backend.adapter.driving.Result;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 测试请求 Controller
 */
@RestController
public class TestRequestController {

    private static final Logger logger = LoggerFactory.getLogger(TestRequestController.class);

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request", method = RequestMethod.POST)
    public Result testRequest(HttpServletRequest request, @RequestBody String requestBody) {
        return Result.okData(Map.of(
                "requestHeaders", new ArrayList<>(Collections.list(request.getHeaderNames())),
                 "requestBody", requestBody
        ));
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request/string-required", method = RequestMethod.POST)
    public Result testRequestStringRequired(@RequestBody String requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request/string", method = RequestMethod.POST)
    public Result testRequestString(@RequestBody(required = false) String requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request/jsonnode-required", method = RequestMethod.POST)
    public Result testRequestJsonNodeRequired(@RequestBody JsonNode requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request/jsonnode", method = RequestMethod.POST)
    public Result testRequestJsonNode(@RequestBody(required = false) JsonNode requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request/object-required", method = RequestMethod.POST)
    public Result testRequestObjectRequired(@RequestBody Object requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request/object", method = RequestMethod.POST)
    public Result testRequestObject(@RequestBody(required = false) Object requestBody) {
        return Result.okData(requestBody);
    }

    //==================================================================================================================

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request/param", method = RequestMethod.GET)
    public Result testRequestParam(@RequestParam(required = false, name = "id", defaultValue = "0") String id) {
        return Result.okData(id);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/api/test/request/path/{path}", method = RequestMethod.GET)
    public Result testRequestPath(@PathVariable String path) {
        return Result.okData(path);
    }

}
