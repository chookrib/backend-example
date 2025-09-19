package com.example.ddd.utility;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDateTime;

/**
 * 值转换工具类
 */
public class ValueUtility {

    /**
     * 日期时间转字符串
     */
    public static String toDateTimeString(LocalDateTime value){
        if (value == null)
            return "";

        return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 字符串转日期时间
     */
    public static LocalDateTime toDateTimeOrDefault(String value, LocalDateTime defaultValue) {
        //try {
        //    return DateUtils.parseDate(value, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd");
        //} catch (Exception e) {
        //    return null;
        //}

        if (StringUtils.isBlank(value))
            return defaultValue;

        try {
            return LocalDateTime.parse(value, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 字符串转日期时间
     */
    public static LocalDateTime toDateTimeOrNull(String value){
        return toDateTimeOrDefault(value, null);
    }

    /**
     * 字符串转日期时间
     */
    public static LocalDateTime toDateTimeReq(String value){
        LocalDateTime dateTime = toDateTimeOrDefault(value, null);
        if (dateTime == null) {
            // throw new IllegalArgumentException("无效的日期时间格式");
            throw new RuntimeException("无效的日期时间格式");
        }
        return dateTime;
    }

    //==================================================================================================================

    /**
     * 字符串转Integer，不成功返回null
     */
    public static Integer toIntOrNull(String value) {
        if(StringUtils.isBlank(value))
            return null;

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 字符串转转int，不成功时返回默认值
     */
    public static int toIntOrDefault(String value, int defaultValue) {
        if(StringUtils.isBlank(value))
            return defaultValue;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 字符串转转int，不成功时抛出异常
     */
    public static int toIntReq(String value, String message) {
        if(StringUtils.isBlank(value))
            throw new RuntimeException(message);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }

}
