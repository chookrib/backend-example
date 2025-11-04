package com.example.backend.adapter.driving;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

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
    public Result testRequestObjectRequired(@RequestBody Object requestBody) {
        return Result.okData(requestBody);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/object", method = RequestMethod.POST)
    public Result testRequestObject(@RequestBody(required = false) Object requestBody) {
        return Result.okData(requestBody);
    }

    //==================================================================================================================

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/param", method = RequestMethod.GET)
    public Result testRequestParam(@RequestParam(value = "id", defaultValue = "0") String id) {
        return Result.okData(id);
    }

    /**
     * 测试请求
     */
    @RequestMapping(value = "/.well-known/test/request/path/{path}", method = RequestMethod.GET)
    public Result testRequestPath(@PathVariable String path) {
        return Result.okData(path);
    }

}
