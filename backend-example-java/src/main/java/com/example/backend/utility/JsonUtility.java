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
        } catch (JsonProcessingException ex) {
            throw new UtilityException("反序列化 JSON 异常", ex);
        }
    }

    /**
     * 序列化对象为 JSON 字符串
     */
    public static String serialize(Object data) {
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            throw new UtilityException("序列化 JSON 异常", ex);
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
    //    //} catch (Exception ex) {
    //    //    throw new UtilityException("对象转 Map 异常", ex);
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
        } catch (JsonProcessingException ex) {
            throw new UtilityException("反序列化 JSON 异常", ex);
        }
    }

    /**
     * 反序列化 JSON 字符串为为指定类型对象
     */
    public static <T> T deserialize(JsonNode data, TypeReference<T> type) {
        //try {
            //return new ObjectMapper().readValue(data, type);
            return new ObjectMapper().convertValue(data, type);
        //} catch (JsonProcessingException ex) {
        //    throw new UtilityException("反序列化 JSON 异常", ex);
        //}
    }

    /**
     * 创建 ObjectNode
     */
    public static ObjectNode createObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode();
    }
}
