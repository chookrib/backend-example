package com.example.ddd.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Jackson工具类
 */
public class JacksonUtility {

    /**
     * 读取JsonNode
     */
    public static JsonNode readTree(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析JSON字符串异常", e);
        }
    }

    /**
     * 将对象转换为Json字符串
     */
    public static String writeValueAsString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("生成JSON字符串异常", e);
        }
    }

    ///**
    // * 将对象转换为Map
    // */
    //public static Map<String, ?> convertValue(Object object) {
    //    //try {
    //        return new ObjectMapper()
    //                //.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
    //                .convertValue(object, Map.class);
    //    //} catch (Exception e) {
    //    //    throw new RuntimeException("对象转为Map异常", e);
    //    //}
    //}
}
