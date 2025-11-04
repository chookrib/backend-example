package com.example.backend.adapter.driving;

import com.example.backend.utility.JsonUtility;
import com.example.backend.utility.ValueUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求 Value Helper
 */
public class RequestValueHelper {

    /**
     * 获取请求体 json 数据
     */
    public static JsonNode getRequestJson(String requestBody) {
        try {
            return JsonUtility.deserialize(requestBody);
        } catch (Exception ex) {
            throw new ControllerException("请求体不是合法的JSON格式", ex);
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

    /**
     * 获取请求 json 数据中 string 数组值
     */
    public static List<String> getRequestJsonStringTrimList(JsonNode json, String... keys) {
        JsonNode node = getRequestJsonValue(json, keys);
        List<String> ss = new ArrayList<>();
        if(node.isMissingNode() || !node.isArray())
            return ss;
        for (JsonNode n : node) {
            ss.add(node.asText().trim());
        }
        return ss;
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
     * 获取请求参数中 string 值，失败返回默认值
     */
    public static String getRequestParamStringTrim(HttpServletRequest request, String defaultValue, String key) {
        String value = request.getParameter(key);
        if(value == null)
            return defaultValue;
        return value;
    }

    /**
     * 获取请求参数中 string 值，失败返回空字符串
     */
    public static String getRequestParamStringTrimOrEmpty(HttpServletRequest request, String key) {
        return getRequestParamStringTrim(request, "", key);
    }

    /**
     * 获取请求参数中 string 值，失败抛出异常
     */
    public static String getRequestParamStringTrimReq(HttpServletRequest request, String key) {
        String value = request.getParameter(key);
        if(value == null)
            throw new ControllerException(String.format("请求参数 %s 值缺失", key));
        return value;
    }

    //==================================================================================================================

    /**
     * 获取请求参数中 bool 值，失败返回默认值
     */
    public static boolean getRequestParamBool(HttpServletRequest request, boolean defaultValue, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        return ValueUtility.toBoolOrDefault(value, defaultValue);
    }

    /**
     * 获取请求参数中 bool 值，失败抛出异常
     */
    public static boolean getRequestParamBoolReq(HttpServletRequest request, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        Boolean b = ValueUtility.toBoolOrNull(value);
        if (b == null)
            throw new ControllerException(String.format("请求参数 %s 值不是合法的 bool", key));
        return b;
    }

    //==================================================================================================================

    /**
     * 获取请求参数中 int 值，失败返回默认值
     */
    public static int getRequestParamInt(HttpServletRequest request, int defaultValue, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        return ValueUtility.toIntOrDefault(value, defaultValue);
    }

    /**
     * 获取请求参数中 int 值，失败抛出异常
     */
    public static int getRequestParamIntReq(HttpServletRequest request, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        Integer i = ValueUtility.toIntOrNull(value);
        if (i == null)
            throw new ControllerException(String.format("请求参数 %s 值不是合法的 int", key));
        return i;
    }

    //==================================================================================================================

    /**
     * 获取请求参数中 long 值，失败返回默认值
     */
    public static long getRequestParamLong(HttpServletRequest request, long defaultValue, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        return ValueUtility.toLongOrDefault(value, defaultValue);
    }

    /**
     * 获取请求参数中 long 值，失败抛出异常
     */
    public static long getRequestParamLongReq(HttpServletRequest request, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        Long l = ValueUtility.toLongOrNull(value);
        if (l == null)
            throw new ControllerException(String.format("请求参数 %s 值不是合法的 long", key));
        return l;
    }

    //==================================================================================================================

    /**
     * 获取请求参数中 datetime 值，失败返回默认值
     */
    public static LocalDateTime getRequestParamDateTime(HttpServletRequest request, LocalDateTime defaultValue, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        return ValueUtility.toDateTimeOrDefault(value, defaultValue);
    }

    /**
     * 获取请求参数中 datetime 值，失败抛出异常
     */
    public static LocalDateTime getRequestParamDateTimeReq(HttpServletRequest request, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        LocalDateTime dt = ValueUtility.toDateTimeOrNull(value);
        if (dt == null)
            throw new ControllerException(String.format("请求参数 %s 值不是合法的 datetime", key));
        return dt;
    }

    //==================================================================================================================


    /**
     * 获取请求参数中 date 值，失败返回默认值
     */
    public static LocalDate getRequestParamDate(HttpServletRequest request, LocalDate defaultValue, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        return ValueUtility.toDateOrDefault(value, defaultValue);
    }

    /**
     * 获取请求参数中 date 值，失败抛出异常
     */
    public static LocalDate getRequestParamDateReq(HttpServletRequest request, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        LocalDate d = ValueUtility.toDateOrNull(value);
        if (d == null)
            throw new ControllerException(String.format("请求参数 %s 值不是合法的 date", key));
        return d;
    }

    //==================================================================================================================


    /**
     * 获取请求参数中 time 值，失败返回默认值
     */
    public static LocalTime getRequestParamTime(HttpServletRequest request, LocalTime defaultValue, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        return ValueUtility.toTimeOrDefault(value, defaultValue);
    }

    /**
     * 获取请求参数中 time 值，失败抛出异常
     */
    public static LocalTime getRequestParamTimeReq(HttpServletRequest request, String key) {
        String value = getRequestParamStringTrimOrEmpty(request, key);
        LocalTime t = ValueUtility.toTimeOrNull(value);
        if (t == null)
            throw new ControllerException(String.format("请求参数 %s 值不是合法的 time", key));
        return t;
    }

    //==================================================================================================================

    /**
     * 分页信息
     */
    public record Paging(int pageNum, int pageSize, int totalCount) { }

    /**
     * 验证并修正分页参数
     */
    public static Paging fixPaging(int pageNum, int pageSize, int totalCount) {
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

        return new Paging(pageNum, pageSize, totalCount);
    }
}
