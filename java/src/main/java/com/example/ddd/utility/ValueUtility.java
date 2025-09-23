package com.example.ddd.utility;

import java.time.LocalDateTime;

/**
 * 值 Utility
 */
public class ValueUtility {

    /**
     * 判断字符串是否null或空字符串
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();    // isEmpty()
    }

    //==================================================================================================================

    /**
     * 取Integer，失败返回null
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
     * 取int，失败返回默认值
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
     * 取int，失败抛出异常
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
     * 取int，失败抛出异常
     */
    public static int toIntReq(String value) {
        return toIntReq(value, "");
    }

    //==================================================================================================================

    /**
     * 日期时间转字符串
     */
    public static String toDateTimeStr(LocalDateTime value) {
        if (value == null)
            return "";

        return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 取日期时间，失败返回默认值
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
     * 取日期时间，失败返回null
     */
    public static LocalDateTime toDateTimeOrNull(String value) {
        return toDateTimeOrDefault(value, null);
    }

    /**
     * 取日期时间，失败抛出异常
     */
    public static LocalDateTime toDateTimeReq(String value, String name) {
        LocalDateTime dateTime = toDateTimeOrDefault(value, null);
        if (dateTime == null) {
            throw new RuntimeException(ValueUtility.isBlank(name) ? "取日期时间值失败" : String.format("取日期时间值 %s 失败", name));
        }
        return dateTime;
    }

    /**
     * 取日期时间，失败抛出异常
     */
    public static LocalDateTime toDateTimeReq(String value) {
        return toDateTimeReq(value, "");
    }
}
