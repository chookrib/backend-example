package com.example.ddd.adapter.driving;

import com.example.ddd.utility.JsonUtility;
import com.example.ddd.utility.ValueUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 请求 Value Helper
 */
public class RequestValueHelper {

    /**
     * 获取请求体 json 数据
     */
    public static JsonNode toJson(String requestBody) {
        try {
            return JsonUtility.deserialize(requestBody);
        } catch (Exception e) {
            throw new ControllerException("请求体不是合法的JSON格式");
        }
    }

    //==================================================================================================================

    /**
     * 获取请求 json 数据中的值，失败返回 None
     */
    public static JsonNode getRequestJsonValue(JsonNode json, String... keys) {
        if (keys == null || keys.length == 0)
            return MissingNode.getInstance();
        JsonNode node = json;
        for (String key : keys) {
            node = node.path(key);
            if(node.isMissingNode())
                return MissingNode.getInstance();
        }
        return node;
    }

    /**
     * 获取请求 json 数据中的值，失败抛出异常
     */
    public static JsonNode getRequestJsonValueReq(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        if (node.isMissingNode()) {
            throw new ControllerException(String.format("请求体缺少 %s", String.join(".", keys)));
        }
        return node;
    }

    //==================================================================================================================

    /**
     * 获取请求 json 数据中 string 值，失败返回默认值
     */
    public static String getRequestJsonStringTrim(JsonNode json, String defaultValue, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        if (node.isMissingNode())
            return defaultValue;
        return node.asText().trim();
    }

    /**
     * 获取请求 json 数据中 string 值，失败返回空字符串
     */
    public static String getRequestJsonStringTrimOrEmpty(JsonNode json, String... keys) {
        return getRequestJsonStringTrim(json, "", keys);
    }

    /**
     * 获取请求 json 数据中 string 值，失败抛出异常
     */
    public static String getRequestJsonStringTrimReq(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValueReq(json, keys);
        return node.asText().trim();
    }

    //==================================================================================================================

    /**
     * 获取请求 json 数据中 bool 值，失败返回默认值
     */
    public static boolean getRequestJsonBool(JsonNode json, boolean defaultValue, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        if (node.isMissingNode())
            return defaultValue;
        if (node.isBoolean())
            return node.booleanValue();
        return ValueUtility.toBoolOrDefault(node.asText(), defaultValue);
    }

    /**
     * 获取请求 json 数据中 bool 值，失败抛出异常
     */
    public static boolean getRequestJsonBoolReq(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValueReq(json, keys);
        if (node.isBoolean())
            return node.booleanValue();
        Boolean b = ValueUtility.toBoolOrNull(node.asText());
        if (b == null)
            throw new ControllerException(String.format("请求体 %s 值不是合法的 bool", String.join(".", keys)));
        return b;
    }

    //==================================================================================================================

    /**
     * 获取请求 json 数据中 int 值，失败返回默认值
     */
    public static int getRequestJsonInt(JsonNode json, int defaultValue, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        if (node.isMissingNode())
            return defaultValue;
        if (node.isInt())
            return node.intValue();
        return ValueUtility.toIntOrDefault(node.asText(), defaultValue);
    }

    /**
     * 获取请求 json 数据中 int 值，失败抛出异常
     */
    public static int getRequestJsonIntReq(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValueReq(json, keys);
        if (node.isInt())
            return node.intValue();
        Integer i = ValueUtility.toIntOrNull(node.asText());
        if (i == null)
            throw new ControllerException(String.format("请求体 %s 值不是合法的 int", String.join(".", keys)));
        return i;
    }

    //==================================================================================================================

    /**
     * 获取请求 json 数据中 long 值，失败返回默认值
     */
    public static long getRequestJsonLong(JsonNode json, long defaultValue, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        if (node.isMissingNode())
            return defaultValue;
        if (node.isLong())
            return node.longValue();
        return ValueUtility.toLongOrDefault(node.asText(), defaultValue);
    }

    /**
     * 获取请求 json 数据中 long 值，失败抛出异常
     */
    public static long getRequestJsonLongReq(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValueReq(json, keys);
        if (node.isLong())
            return node.longValue();
        Long l = ValueUtility.toLongOrNull(node.asText());
        if (l == null)
            throw new ControllerException(String.format("请求体 %s 值不是合法的 long", String.join(".", keys)));
        return l;
    }

    //==================================================================================================================

    /**
     * 获取请求 json 数据中 datetime 值，失败返回默认值
     */
    public static LocalDateTime getRequestJsonDateTime(JsonNode json, LocalDateTime defaultValue, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        if (node.isMissingNode())
            return defaultValue;
        return ValueUtility.toDateTimeOrDefault(node.asText(), defaultValue);
    }

    /**
     * 获取请求 json 数据中 datetime 值，失败抛出异常
     */
    public static LocalDateTime getRequestJsonDateTimeReq(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValueReq(json, keys);
        LocalDateTime dt = ValueUtility.toDateTimeOrNull(node.asText());
        if (dt == null)
            throw new ControllerException(String.format("请求体 %s 值不是合法的 datetime", String.join(".", keys)));
        return dt;
    }

    //==================================================================================================================


    /**
     * 获取请求 json 数据中 date 值，失败返回默认值
     */
    public static LocalDate getRequestJsonDate(JsonNode json, LocalDate defaultValue, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        if (node.isMissingNode())
            return defaultValue;
        return ValueUtility.toDateOrDefault(node.asText(), defaultValue);
    }

    /**
     * 获取请求 json 数据中 date 值，失败抛出异常
     */
    public static LocalDate getRequestJsonDateReq(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValueReq(json, keys);
        LocalDate d = ValueUtility.toDateOrNull(node.asText());
        if (d == null)
            throw new ControllerException(String.format("请求体 %s 值不是合法的 date", String.join(".", keys)));
        return d;
    }

    //==================================================================================================================


    /**
     * 获取请求 json 数据中 time 值，失败返回默认值
     */
    public static LocalTime getRequestJsonTime(JsonNode json, LocalTime defaultValue, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        if (node.isMissingNode())
            return defaultValue;
        return ValueUtility.toTimeOrDefault(node.asText(), defaultValue);
    }

    /**
     * 获取请求 json 数据中 time 值，失败抛出异常
     */
    public static LocalTime getRequestJsonTimeReq(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValueReq(json, keys);
        LocalTime t = ValueUtility.toTimeOrNull(node.asText());
        if (t == null)
            throw new ControllerException(String.format("请求体 %s 值不是合法的 time", String.join(".", keys)));
        return t;
    }

    //==================================================================================================================

    /**
     * 分页信息
     */
    public record PagingInfo(int pageNum, int pageSize, int totalCount) { }

    /**
     * 验证并修正分页参数
     */
    public static PagingInfo fixPaging(int pageNum, int pageSize, int totalCount) {
        if (pageSize < 1)
            pageSize = 1;
        if (totalCount < 0)
            totalCount = 0;

        int maxPageNum = totalCount / pageSize;
        if (totalCount % pageSize > 0)
            maxPageNum++;

        if (pageNum > maxPageNum)
            pageNum = maxPageNum;
        if (pageNum < 1)
            pageNum = 1;

        return new PagingInfo(pageNum, pageSize, totalCount);
    }
}
