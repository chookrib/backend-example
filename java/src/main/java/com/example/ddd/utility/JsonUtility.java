package com.example.ddd.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Json Utility
 */
public class JsonUtility {

    /**
     * 读取 JSON 字符串为 JsonNode
     */
    public static JsonNode readTree(String value) {
        try {
            return new ObjectMapper().readTree(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析JSON字符串异常", e);
        }
    }

    /**
     * 将对象转换为 JSON 字符串
     */
    public static String writeValueAsString(Object value) {
        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("生成JSON字符串异常", e);
        }
    }

    ///**
    // * 将对象转换为Map
    // */
    //public static Map<String, ?> convertValue(Object value) {
    //    //try {
    //        return new ObjectMapper()
    //                //.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
    //                .convertValue(value, Map.class);
    //    //} catch (Exception e) {
    //    //    throw new RuntimeException("对象转为Map异常", e);
    //    //}
    //}

    /**
     * 读取 JSON 字符串为 JsonNode，允许未转义的控制字符
     */
    public static JsonNode readTreeAllowUnescapedControlChars(String content) {
        ObjectMapper objectMapper = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .build();
        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取 JSON 字符串为指定类型对象
     */
    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        try {
            return new ObjectMapper().readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建 ObjectNode
     */
    public static ObjectNode createObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode();
    }
}
