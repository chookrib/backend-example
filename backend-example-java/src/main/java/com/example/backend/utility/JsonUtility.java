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
public class JsonUtility {

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
     * 反序列化 JSON 字符串为 JsonNode
     */
    public static JsonNode deserialize(String data) {
        try {
            return OBJECT_MAPPER.readTree(data);
        } catch (JsonProcessingException ex) {
            throw new UtilityException("反序列化 JSON 异常", ex);
        }
    }

    /**
     * 序列化对象为 JSON 字符串
     */
    public static String serialize(Object data) {
        try {
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            throw new UtilityException("序列化 JSON 异常", ex);
        }
    }

    /**
     * 反序列化 JSON 字符串为为指定类型对象
     */
    public static <T> T deserialize(JsonNode data, TypeReference<T> type) {
        // try {
        // return OBJECT_MAPPER.readValue(data, type);
        return OBJECT_MAPPER.convertValue(data, type);
        // } catch (JsonProcessingException ex) {
        //     throw new UtilityException("反序列化 JSON 异常", ex);
        // }
    }

    /**
     * 创建 ObjectNode
     */
    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }
}
