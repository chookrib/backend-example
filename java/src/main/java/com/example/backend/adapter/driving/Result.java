package com.example.backend.adapter.driving;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller 返回结果
 */
public class Result {

    private int code;           //返回码
    private String message;     //返回消息
    private Object data;        //返回数据

    /**
     * 构造函数
     */
    private Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     */
    private Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    /**
     * 是否成功
     */
    //@JsonProperty("isSuccess")
    public boolean isSuccess() {
        return code == ResultCodes.SUCCESS;
    }

    /**
     * 成功
     */
    public static Result ok() {
        return new Result(ResultCodes.SUCCESS, "");
    }

    /**
     * 成功，指定信息
     */
    public static Result ok(String message) {
        return new Result(ResultCodes.SUCCESS, message);
    }

    /**
     * 成功，指定信息和数据
     */
    public static Result ok(String message, Object data) {
        return new Result(ResultCodes.SUCCESS, message, data);
    }

    /**
     * 成功，指定数据
     */
    public static Result okData(Object data) {
        return new Result(ResultCodes.SUCCESS, "", data);
    }

    /**
     * 失败，指定返回码
     */
    public static Result error(int code) {
        if (code == ResultCodes.SUCCESS)
            code = ResultCodes.ERROR_DEFAULT;
        return new Result(code, "");
    }

    /**
     * 失败，指定返回码和信息
     */
    public static Result error(int code, String message) {
        if (code == ResultCodes.SUCCESS)
            code = ResultCodes.ERROR_DEFAULT;
        return new Result(code, message);
    }

    /**
     * 失败，指定返回码、信息和数据
     */
    public static Result error(int code, String message, Object data) {
        if (code == ResultCodes.SUCCESS)
            code = ResultCodes.ERROR_DEFAULT;
        return new Result(code, message, data);
    }

    /**
     * 转换为Map，便于动态添加属性
     */
    @JsonIgnore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        map.put("data", data);
        map.put("success", isSuccess());
        return map;
    }
}

