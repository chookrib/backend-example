package com.example.backend.utility;

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
     * 反序列化 JSON 字符串为 JsonNode
     */
    public static JsonNode deserialize(String data) {
        try {
            return new ObjectMapper().readTree(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化JSON异常", e);
        }
    }

    /**
     * 序列化对象为 JSON 字符串
     */
    public static String serialize(Object data) {
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化JSON异常", e);
        }
    }

    ///**
    // * 将对象转换为Map
    // */
    //public static Map<String, ?> toMap(Object data) {
    //    //try {
    //        return new ObjectMapper()
    //                //.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
    //                .convertValue(data, Map.class);
    //    //} catch (Exception e) {
    //    //    throw new RuntimeException("对象转为Map异常", e);
    //    //}
    //}

    /**
     * 反序列化 JSON 字符串为 JsonNode，允许未转义的控制字符
     */
    public static JsonNode deserializeAllowUnescapedControlChars(String data) {
        ObjectMapper objectMapper = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .build();
        try {
            return objectMapper.readTree(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反序列化 JSON 字符串为为指定类型对象
     */
    public static <T> T deserialize(String data, TypeReference<T> type) {
        try {
            return new ObjectMapper().readValue(data, type);
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
