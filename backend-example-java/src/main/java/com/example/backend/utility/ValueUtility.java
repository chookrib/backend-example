package com.example.backend.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * 值 Utility
 */
public class ValueUtility {

    /**
     * 判断字符串是否为 null 或空字符串
     */
    public static boolean isEmptyString(String value) {
        return value == null || value.isBlank();    // isEmpty()
    }

    //==================================================================================================================

    // 定义 bool 值范围
    private static final List<String> BOOL_TRUE = Arrays.asList("true", "1", "t", "y", "yes", "on");
    private static final List<String> BOOL_FALSE = Arrays.asList("false", "0", "f", "n", "no", "off");

    /**
     * 转 bool，失败返回 null
     */
    public static Boolean toBoolOrNull(String value) {
        if (isEmptyString(value))
            return null;
        String valueNormalize = value.trim().toLowerCase();
        if (BOOL_TRUE.contains(valueNormalize))
            return true;
        else if (BOOL_FALSE.contains(valueNormalize))
            return false;
        return null;
    }

    /**
     * 转 bool，失败返回默认值
     */
    public static boolean toBoolOrDefault(String value, boolean defaultValue) {
        Boolean b = toBoolOrNull(value);
        if (b != null)
            return b;
        return defaultValue;
    }

    //==================================================================================================================

    /**
     * 转 int，失败返回 null
     */
    public static Integer toIntOrNull(String value) {
        if (isEmptyString(value))
            return null;
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 转 int，失败返回默认值
     */
    public static int toIntOrDefault(String value, int defaultValue) {
        Integer i = toIntOrNull(value);
        if (i != null)
            return i;
        return defaultValue;
    }

    //==================================================================================================================

    /**
     * 转 long，失败返回 null
     */
    public static Long toLongOrNull(String value) {
        if (isEmptyString(value))
            return null;
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 转 long，失败返回默认值
     */
    public static long toLongOrDefault(String value, long defaultValue) {
        Long l = toLongOrNull(value);
        if (l != null)
            return l;
        return defaultValue;
    }

    //==================================================================================================================

    // 定义 decimal 边界值 DECIMAL(28, 8)，满足绝大部分场景
    private static final int DECIMAL_SCALE = 8;
    private static final BigDecimal DECIMAL_MIN_VALUE = new BigDecimal("-99999999999999999999.99999999");
    private static final BigDecimal DECIMAL_MAX_VALUE = new BigDecimal("99999999999999999999.99999999");

    /**
     * 转 decimal，失败返回 null
     */
    public static BigDecimal toDecimalOrNull(String value) {
        if (isEmptyString(value))
            return null;
        try {
            // return new BigDecimal(value.trim());

            BigDecimal d = new BigDecimal(value.trim());
            // 规范化小数位（四舍五入）
            d = d.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
            // 检查是否超出边界
            if (d.compareTo(DECIMAL_MIN_VALUE) < 0 || d.compareTo(DECIMAL_MAX_VALUE) > 0) {
                return null;
            }
            return d;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 转 decimal，失败返回默认值
     */
    public static BigDecimal toDecimalOrDefault(String value, BigDecimal defaultValue) {
        BigDecimal d = toDecimalOrNull(value);
        if (d != null)
            return d;
        if (defaultValue == null)
            return null;
        if (defaultValue.compareTo(DECIMAL_MIN_VALUE) < 0 || defaultValue.compareTo(DECIMAL_MAX_VALUE) > 0)
            throw new UtilityException("默认值超出 deciaml 范围");
        return defaultValue;
    }

    //==================================================================================================================
    // datetime 精度统一为毫秒

    /**
     * 获取当前时间，精度到毫秒
     */
    public static LocalDateTime getDateTimeNow() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }

    /**
     * 格式化 datetime，精度到秒
     */
    public static String formatDateTime(LocalDateTime value) {
        if (value != null)
            return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "";
    }

    /**
     * 格式化 datetime，精度到毫秒
     */
    public static String formatDateTimeMillisecond(LocalDateTime value) {
        if (value != null)
            return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        return "";
    }

    // 构建支持可选时间部分的日期时间格式化器
    private static final DateTimeFormatter DATETIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .optionalStart().appendPattern(" HH:mm")
            .optionalStart().appendPattern(":ss")
            // 支持 1 到 9 位小数秒
            .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
            .optionalEnd().optionalEnd().optionalEnd()
            // 如果只有日期，默认时间为 00:00:00.000
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .toFormatter();

    /**
     * 转 datetime，精度到毫秒，失败返回 null
     */
    public static LocalDateTime toDateTimeOrNull(String value) {
        if (isEmptyString(value))
            return null;

        // DateUtils.parseDate(value.trim(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd");
        // LocalDateTime.parse(value.trim(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // try {
        //     return LocalDateTime.parse(value.trim().replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        // } catch (DateTimeParseException ex) {
        //     try {
        //         // 如果只有日期，返回日期 00:00:00
        //         return LocalDate.parse(value.trim(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        //     } catch (DateTimeParseException exc) {
        //         return null;
        //     }
        // }

        String dt = value.trim();

        // 防御性处理：防止输入超过 9 位小数导致解析失败
        int dotIndex = dt.indexOf('.');
        if (dotIndex > 0 && dt.length() - dotIndex - 1 > 9) {
            dt = dt.substring(0, dotIndex + 10);
        }

        try {
            return LocalDateTime.parse(dt, DATETIME_FORMATTER).truncatedTo(ChronoUnit.MILLIS);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    /**
     * 转 datetime，精度到毫秒，失败返回默认值
     */
    public static LocalDateTime toDateTimeOrDefault(String value, LocalDateTime defaultValue) {
        LocalDateTime dt = toDateTimeOrNull(value);
        if (dt != null)
            return dt;
        if (defaultValue == null)
            return null;
        return defaultValue.truncatedTo(ChronoUnit.MILLIS);
    }

    //==================================================================================================================

    /**
     * 格式化 date
     */
    public static String formatDate(LocalDate value) {
        if (value != null)
            return value.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "";
    }

    /**
     * 转 date，失败返回 null
     */
    public static LocalDate toDateOrNull(String value) {
        if (isEmptyString(value))
            return null;

        //  LocalDate.parse(value.trim(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // try {
        //     return LocalDate.parse(value.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        // } catch (DateTimeParseException ex) {
        //     // 如果是带时间的日期时间，提取日期部分
        //     try {
        //         return LocalDateTime.parse(
        //                 value.trim().replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME
        //         ).toLocalDate();
        //     } catch (DateTimeParseException exc) {
        //         return null;
        //     }
        // }

        String d = value.trim();
        if (d.length() < 10)
            return null;

        try {
            return LocalDate.parse(d.substring(0, 10));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 转 date，失败返回默认值
     */
    public static LocalDate toDateOrDefault(String value, LocalDate defaultValue) {
        LocalDate d = toDateOrNull(value);
        if (d != null)
            return d;
        return defaultValue;
    }

    //==================================================================================================================
    // time 精度统一为秒

    /**
     * 格式化 time，精度到秒
     */
    public static String formatTime(LocalTime value) {
        if (value != null)
            return value.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        return "";
    }

    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            // 将 "日期 + 空格" 作为一个独立的可选整体
            .optionalStart()
            .appendPattern("yyyy-MM-dd")
            .appendPattern(" ")
            .optionalEnd()
            // 时间部分必选
            .appendPattern("HH:mm")
            // 秒可选
            .optionalStart()
            .appendPattern(":ss")
            // 纳秒（小数位）可选，且依赖于秒的存在
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
            .optionalEnd()
            .optionalEnd()
            .toFormatter();

    /**
     * 转 time，精度到秒，失败返回 null
     */
    public static LocalTime toTimeOrNull(String value) {
        if (isEmptyString(value))
            return null;

        // try {
        //     // return LocalTime.parse(value.trim(), java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        //     return LocalTime.parse(value.trim(), DateTimeFormatter.ISO_LOCAL_TIME);
        // } catch (DateTimeParseException ex) {
        //     try {
        //         return LocalDateTime.parse(
        //                 value.trim().replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME
        //         ).toLocalTime();
        //     } catch (DateTimeParseException exc) {
        //         return null;
        //     }
        // }

        String t = value.trim();

        // 防御性处理：防止输入超过 9 位小数导致解析失败
        int dotIndex = t.indexOf('.');
        if (dotIndex > 0 && t.length() - dotIndex - 1 > 9) {
            t = t.substring(0, dotIndex + 10);
        }

        try {
            return LocalTime.parse(t, TIME_FORMATTER).truncatedTo(ChronoUnit.SECONDS);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    /**
     * 转 time，精度到秒，失败返回默认值
     */
    public static LocalTime toTimeOrDefault(String value, LocalTime defaultValue) {
        LocalTime t = toTimeOrNull(value);
        if (t != null)
            return t;
        if(defaultValue == null)
            return null;
        return defaultValue.truncatedTo(ChronoUnit.SECONDS);
    }
}
