package com.example.ddd.utility;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

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
     * 转 bool，失败返回 null
     */
    public static Boolean toBoolOrNull(String value) {
        if (isBlank(value))
            return null;
        if (Arrays.asList("true", "1", "t", "y", "yes", "on").contains(value.toLowerCase()))
            return true;
        else if (Arrays.asList("false", "0", "f", "n", "no", "off").contains(value.toLowerCase()))
            return false;
        return null;
    }

    /**
     * 转 bool，失败返回默认值
     */
    public static boolean toBoolOrDefault(String value, boolean defaultValue) {
        Boolean b = toBoolOrNull(value);
        if (b == null)
            return defaultValue;
        return b;
    }

    //==================================================================================================================

    /**
     * 转 int，失败返回 null
     */
    public static Integer toIntOrNull(String value) {
        if (isBlank(value))
            return null;
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 转 int，失败返回默认值
     */
    public static int toIntOrDefault(String value, int defaultValue) {
        Integer i = toIntOrNull(value);
        if (i == null)
            return defaultValue;
        return i;
    }

    //==================================================================================================================

    /**
     * 转 long，失败返回 null
     */
    public static Long toLongOrNull(String value) {
        if (isBlank(value))
            return null;
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 转 long，失败返回默认值
     */
    public static long toLongOrDefault(String value, long defaultValue) {
        Long l = toLongOrNull(value);
        if (l == null)
            return defaultValue;
        return l;
    }

    //==================================================================================================================

    /**
     * 转 decimal，失败返回 null
     */
    public static BigDecimal toDecimalOrNull(String value) {
        if (isBlank(value))
            return null;
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 转 decimal，失败返回默认值
     */
    public static BigDecimal toDecimalOrDefault(String value, BigDecimal defaultValue) {
        BigDecimal d = toDecimalOrNull(value);
        if (d == null)
            return defaultValue;
        return d;
    }

    //==================================================================================================================


    /**
     * 格式化 datetime
     */
    public static String formatDateTime(LocalDateTime value) {
        if (value == null)
            return "";
        return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 转 datetime，失败返回 null
     */
    public static LocalDateTime toDateTimeOrNull(String value) {
        //try {
        //    return DateUtils.parseDate(value, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd");
        //} catch (Exception e) {
        //    return null;
        //}

        if (isBlank(value))
            return null;
        try {
            return LocalDateTime.parse(value, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 转 datetime，失败返回默认值
     */
    public static LocalDateTime toDateTimeOrDefault(String value, LocalDateTime defaultValue) {
        LocalDateTime dt = toDateTimeOrNull(value);
        if (dt == null)
            return defaultValue;
        return dt;
    }

    //==================================================================================================================

    /**
     * 格式化 date
     */
    public static String formatDate(LocalDate value) {
        if (value == null)
            return "";
        return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 转 date，失败返回 null
     */
    public static LocalDate toDateOrNull(String value) {
        if (isBlank(value))
            return null;
        try {
            return LocalDate.parse(value, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 转 date，失败返回默认值
     */
    public static LocalDate toDateOrDefault(String value, LocalDate defaultValue) {
        LocalDate d = toDateOrNull(value);
        if (d == null)
            return defaultValue;
        return d;
    }

    //==================================================================================================================

    /**
     * 格式化 time
     */
    public static String formatTime(LocalTime value) {
        if (value == null)
            return "";
        return value.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * 转 time，失败返回 null
     */
    public static LocalTime toTimeOrNull(String value) {
        if (isBlank(value))
            return null;
        try {
            return LocalTime.parse(value, java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 转 time，失败返回默认值
     */
    public static LocalTime toTimeOrDefault(String value, LocalTime defaultValue) {
        LocalTime t = toTimeOrNull(value);
        if (t == null)
            return defaultValue;
        return t;
    }
}
