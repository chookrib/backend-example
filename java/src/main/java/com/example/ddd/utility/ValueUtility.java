package com.example.ddd.utility;

import java.time.LocalDateTime;

/**
 * 值 Utility
 */
public class ValueUtility {

    /**
     * 判断字符串是否为 null 或空字符串
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();    // isEmpty()
    }

    //==================================================================================================================

    /**
     * String 转 Integer，失败返回 null
     */
    public static Integer toIntOrNull(String value) {
        if (ValueUtility.isBlank(value))
            return null;
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * String 转 int，失败返回默认值
     */
    public static int toIntOrDefault(String value, int defaultValue) {
        if (ValueUtility.isBlank(value))
            return defaultValue;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * String 转 int，失败抛出异常
     */
    public static int toIntReq(String value, String name) {
        if (ValueUtility.isBlank(value))
            throw new RuntimeException(ValueUtility.isBlank(name) ? "取整数值失败" : String.format("取整数值 %s 失败", name));

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(ValueUtility.isBlank(name) ? "取整数值失败" : String.format("取整数值 %s 失败", name));
        }
    }

    /**
     * String 转 int，失败抛出异常
     */
    public static int toIntReq(String value) {
        return toIntReq(value, "");
    }

    //==================================================================================================================

    /**
     * LocalDateTime 格式化 String
     */
    public static String toDateTimeStr(LocalDateTime value) {
        if (value == null)
            return "";

        return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * String 转 LocalDateTime，失败返回默认值
     */
    public static LocalDateTime toDateTimeOrDefault(String value, LocalDateTime defaultValue) {
        //try {
        //    return DateUtils.parseDate(value, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd");
        //} catch (Exception e) {
        //    return null;
        //}

        if (ValueUtility.isBlank(value))
            return defaultValue;

        try {
            return LocalDateTime.parse(value, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * String 转 LocalDateTime，失败返回 null
     */
    public static LocalDateTime toDateTimeOrNull(String value) {
        return toDateTimeOrDefault(value, null);
    }

    /**
     * String 转 LocalDateTime，失败抛出异常，指定异常信息中名称
     */
    public static LocalDateTime toDateTimeReq(String value, String name) {
        LocalDateTime dateTime = toDateTimeOrDefault(value, null);
        if (dateTime == null) {
            throw new RuntimeException(ValueUtility.isBlank(name) ? "取日期时间值失败" : String.format("取日期时间值 %s 失败", name));
        }
        return dateTime;
    }

    /**
     * String 转 LocalDateTime，失败抛出异常
     */
    public static LocalDateTime toDateTimeReq(String value) {
        return toDateTimeReq(value, "");
    }
}
