package com.example.backend.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JSON Utility
 */
public final class JsonUtility {

    private static final ObjectMapper OBJECT_MAPPER = initObjectMapper();

    /**
     * 初始化静态 ObjectMapper
     */
    private static ObjectMapper initObjectMapper() {
        return JsonMapper.builder()
                // .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)   // 允许未转义的控制字符
                .build();
    }

    /**
     * 序列化对象为 JSON 字符串，失败抛出指定消息异常
     */
    public static String serialize(Object data, String message) {
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            throw new UtilityException(message, ex);
        }
    }

    /**
     * 序列化对象为 JSON 字符串，失败抛出异常
     */
    public static String serialize(Object data) {
        return serialize(data, "序列化 JSON 失败");
    }

    /**
     * 反序列化 JSON 字符串为 JsonNode，失败抛出指定消息异常
     */
    public static JsonNode deserialize(String data, String message) {
        try {
            return OBJECT_MAPPER.readTree(data);
        } catch (JsonProcessingException ex) {
            throw new UtilityException(message, ex);
        }
    }

    /**
     * 反序列化 JSON 字符串为 JsonNode，失败抛出异常
     */
    public static JsonNode deserialize(String data) {
        return deserialize(data, "反序列化 JSON 失败");
    }

    /**
     * 反序列化 JSON 字符串为指定类型对象，失败抛出指定消息异常
     */
    public static <T> T deserialize(String data, TypeReference<T> type, String message) {
        try {
            return OBJECT_MAPPER.readValue(data, type);
            // JsonNode json = deserialize(data);
            // return OBJECT_MAPPER.convertValue(json, type);
        } catch (Exception ex) {
            throw new UtilityException(message, ex);
        }
    }

    /**
     * 反序列化 JSON 字符串为为指定类型对象，失败抛出异常
     */
    public static <T> T deserialize(String data, TypeReference<T> type) {
        return deserialize(data, type, "反序列化 JSON 为指定类型失败");
    }

    /**
     * 反序列化 JsonNode 为指定类型对象，失败抛出指定消息异常
     */
    public static <T> T deserialize(JsonNode data, TypeReference<T> type, String message) {
        try {
            return OBJECT_MAPPER.convertValue(data, type);
        } catch (Exception ex) {
            throw new UtilityException(message, ex);
        }
    }

    /**
     * 反序列化 JsonNode 为指定类型对象，失败抛出异常
     */
    public static <T> T deserialize(JsonNode data, TypeReference<T> type) {
        return deserialize(data, type, "反序列化 JSON 为指定类型失败");
    }

    //==================================================================================================================

    /**
     * 转义字符串
     */
    public static String escapeString(String s) {
        try {
            return OBJECT_MAPPER.writeValueAsString(s);
        } catch (JsonProcessingException e) {
            throw new UtilityException("转义 JSON 字符串失败");
        }
    }

    /**
     * 创建 ObjectNode
     */
    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }
}
