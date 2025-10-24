package com.example.ddd.adapter.driving;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试请求 Well Known Controller
 */
@RestController
public class WellKnownTestRequestController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownTestRequestController.class);

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/string-required", method = RequestMethod.POST)
    public Result testRequestStringRequired(@RequestBody String requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/string", method = RequestMethod.POST)
    public Result testRequestString(@RequestBody(required = false) String requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/jsonnode-required", method = RequestMethod.POST)
    public Result testRequestJsonNodeRequired(@RequestBody JsonNode requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/jsonnode", method = RequestMethod.POST)
    public Result testRequestJsonNode(@RequestBody(required = false) JsonNode requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/object-required", method = RequestMethod.POST)
    public Result testRequestJsonNodeRequired(@RequestBody Object requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/object", method = RequestMethod.POST)
    public Result testRequestJsonNode(@RequestBody(required = false) Object requestBody) {
        return Result.okData(requestBody);
    }
}
