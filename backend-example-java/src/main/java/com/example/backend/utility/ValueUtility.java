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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 值 Utility
 */
public final class ValueUtility {

    /**
     * 当值为 null 时抛出异常，否则返回值
     */
    public static <T> T requireNonNull(T value) {
        if (value == null)
            throw new UtilityException("值不能为 null");
        return value;
    }

    /**
     * 当值为 null 时抛出指定消息异常，否则返回值
     */
    public static <T> T requireNonNull(T value, String message) {
        if (value == null)
            throw new UtilityException(message);
        return value;
    }

    /**
     * 当值为 null 时返回默认值，否则返回值，此方法不判断提供的默认值是否为 null
     */
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    // =================================================================================================================

    /**
     * 判断字符串是否为 null 或空字符串（包括仅含空格的字符串）
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();    // value.isEmpty()
    }

    /**
     * 当值为 null 或空字符串（包括仅含空格的字符串）时抛出异常，否则返回值
     */
    public static String requireNotBlank(String value) {
        if (isBlank(value))
            throw new UtilityException("值不能为空字符串");
        return value;
    }

    /**
     * 当值为 null 或空字符串（包括仅含空格的字符串）时抛出指定消息异常，否则返回值
     */
    public static String requireNotBlank(String value, String message) {
        if (isBlank(value))
            throw new UtilityException(message);
        return value;
    }

    /**
     * 当值为 null 时返回空字符串 ""，否则返回值
     */
    public static String emptyIfNull(String value) {
        return value != null ? value : "";
    }

    // =================================================================================================================

    // 定义 bool 值范围
    private static final List<String> BOOL_TRUE = List.of("true", "1", "t", "y", "yes", "on");
    private static final List<String> BOOL_FALSE = List.of("false", "0", "f", "n", "no", "off");

    /**
     * 转 bool，失败返回 null
     */
    public static Boolean toBoolOrNull(String value) {
        if (isBlank(value))
            return null;
        String b = value.trim().toLowerCase();
        if (BOOL_TRUE.contains(b))
            return true;
        else if (BOOL_FALSE.contains(b))
            return false;
        return null;
    }

    // /**
    //  * 转 bool，失败返回默认值
    //  */
    // public static boolean toBoolOrDefault(String value, boolean defaultValue) {
    //     Boolean b = toBoolOrNull(value);
    //     if (b != null)
    //         return b;
    //     return defaultValue;
    // }

    /**
     * 转 bool，失败抛出指定消息异常
     */
    public static boolean toBoolOrThrow(String value, String message) {
        Boolean b = toBoolOrNull(value);
        if (b == null)
            throw new UtilityException(message);
        return b;
    }

    /**
     * 转 bool，失败抛出异常
     */
    public static boolean toBoolOrThrow(String value) {
        return toBoolOrThrow(value, "无效的 bool 值");
    }

    /**
     * 转 bool，传入值为 null 或空字符串返回 null，否则抛出指定消息异常
     */
    public static Boolean toBoolNullOrThrow(String value, String message) {
        if (isBlank(value))
            return null;
        return toBoolOrThrow(value, message);
    }

    /**
     * 转 bool，传入值为 null 或空字符串返回 null，否则抛出异常
     */
    public static Boolean toBoolNullOrThrow(String value) {
        if (isBlank(value))
            return null;
        return toBoolOrThrow(value);
    }

    // =================================================================================================================

    /**
     * 转 int，失败返回 null
     */
    public static Integer toIntOrNull(String value) {
        if (isBlank(value))
            return null;
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    // /**
    //  * 转 int，失败返回默认值
    //  */
    // public static int toIntOrDefault(String value, int defaultValue) {
    //     Integer i = toIntOrNull(value);
    //     if (i != null)
    //         return i;
    //     return defaultValue;
    // }

    /**
     * 转 int，失败抛出指定消息异常
     */
    public static int toIntOrThrow(String value, String message) {
        Integer i = toIntOrNull(value);
        if (i == null)
            throw new UtilityException(message);
        return i;
    }

    /**
     * 转 int，失败抛出异常
     */
    public static int toIntOrThrow(String value) {
        return toIntOrThrow(value, "无效的 int 值");
    }

    /**
     * 转 int，传入值为 null 或空字符串返回 null，否则抛出指定消息异常
     */
    public static Integer toIntNullOrThrow(String value, String message) {
        if (isBlank(value))
            return null;
        return toIntOrThrow(value, message);
    }

    /**
     * 转 int，传入值为 null 或空字符串返回 null，否则抛出异常
     */
    public static Integer toIntNullOrThrow(String value) {
        if (isBlank(value))
            return null;
        return toIntOrThrow(value);
    }

    // =================================================================================================================

    /**
     * 转 long，失败返回 null
     */
    public static Long toLongOrNull(String value) {
        if (isBlank(value))
            return null;
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    // /**
    //  * 转 long，失败返回默认值
    //  */
    // public static long toLongOrDefault(String value, long defaultValue) {
    //     Long l = toLongOrNull(value);
    //     if (l != null)
    //         return l;
    //     return defaultValue;
    // }

    /**
     * 转 long，失败抛出指定消息异常
     */
    public static long toLongOrThrow(String value, String message) {
        Long l = toLongOrNull(value);
        if (l == null)
            throw new UtilityException(message);
        return l;
    }

    /**
     * 转 long，失败抛出异常
     */
    public static long toLongOrThrow(String value) {
        return toLongOrThrow(value, "无效的 long 值");
    }

    /**
     * 转 long，传入值为 null 或空字符串返回 null，否则抛出指定消息异常
     */
    public static Long toLongNullOrThrow(String value, String message) {
        if (isBlank(value))
            return null;
        return toLongOrThrow(value, message);
    }

    /**
     * 转 long，传入值为 null 或空字符串返回 null，否则抛出异常
     */
    public static Long toLongNullOrThrow(String value) {
        if (isBlank(value))
            return null;
        return toLongOrThrow(value);
    }

    // =================================================================================================================

    // 定义 decimal 边界值 DECIMAL(28, 8)，满足绝大部分场景
    private static final int DECIMAL_SCALE = 8;
    private static final BigDecimal DECIMAL_MIN_VALUE = new BigDecimal("-99999999999999999999.99999999");
    private static final BigDecimal DECIMAL_MAX_VALUE = new BigDecimal("99999999999999999999.99999999");

    /**
     * 转 decimal，失败返回 null
     */
    public static BigDecimal toDecimalOrNull(String value) {
        if (isBlank(value))
            return null;
        try {
            // return new BigDecimal(value.trim());

            BigDecimal d = new BigDecimal(value.trim());
            // 规范化小数位（四舍五入）
            d = d.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
            // 去掉末尾多余的 0
            d = d.stripTrailingZeros();
            // 检查是否超出边界
            if (d.compareTo(DECIMAL_MIN_VALUE) < 0 || d.compareTo(DECIMAL_MAX_VALUE) > 0) {
                return null;
            }
            return d;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    // /**
    //  * 转 decimal，失败返回默认值
    //  */
    // public static BigDecimal toDecimalOrDefault(String value, BigDecimal defaultValue) {
    //     BigDecimal d = toDecimalOrNull(value);
    //     if (d != null)
    //         return d;
    //     if (defaultValue == null)
    //         return null;
    //     if (defaultValue.compareTo(DECIMAL_MIN_VALUE) < 0 || defaultValue.compareTo(DECIMAL_MAX_VALUE) > 0)
    //         throw new UtilityException("默认值超出 decimal 范围");
    //     return defaultValue;
    // }

    /**
     * 转 decimal，失败抛出指定消息异常
     */
    public static BigDecimal toDecimalOrThrow(String value, String message) {
        BigDecimal d = toDecimalOrNull(value);
        if (d == null)
            throw new UtilityException(message);
        return d;
    }

    /**
     * 转 decimal，失败抛出异常
     */
    public static BigDecimal toDecimalOrThrow(String value) {
        return toDecimalOrThrow(value, "无效的 decimal 值");
    }

    /**
     * 转 decimal，传入值为 null 或空字符串返回 null，否则抛出指定消息异常
     */
    public static BigDecimal toDecimalNullOrThrow(String value, String message) {
        if (isBlank(value))
            return null;
        return toDecimalOrThrow(value, message);
    }

    /**
     * 转 decimal，传入值为 null 或空字符串返回 null，否则抛出异常
     */
    public static BigDecimal toDecimalNullOrThrow(String value) {
        if (isBlank(value))
            return null;
        return toDecimalOrThrow(value);
    }

    // =================================================================================================================
    // datetime 精度统一为毫秒

    /**
     * 获取当前时间，精度到毫秒
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }

    /**
     * 格式化 datetime，精度到秒
     */
    public static String formatDateTime(LocalDateTime value) {
        if (value != null)
            return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "";
    }

    /**
     * 格式化 datetime，精度到毫秒
     */
    public static String formatDateTimeMillisecond(LocalDateTime value) {
        if (value != null)
            return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
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
        if (isBlank(value))
            return null;

        // DateUtils.parseDate(value.trim(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd");
        // LocalDateTime.parse(value.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

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

    // /**
    //  * 转 datetime，精度到毫秒，失败返回默认值
    //  */
    // public static LocalDateTime toDateTimeOrDefault(String value, LocalDateTime defaultValue) {
    //     LocalDateTime dt = toDateTimeOrNull(value);
    //     if (dt != null)
    //         return dt;
    //     if (defaultValue == null)
    //         return null;
    //     return defaultValue.truncatedTo(ChronoUnit.MILLIS);
    // }

    /**
     * 转 datetime，失败抛出指定消息异常
     */
    public static LocalDateTime toDateTimeOrThrow(String value, String message) {
        LocalDateTime dt = toDateTimeOrNull(value);
        if (dt == null)
            throw new UtilityException(message);
        return dt;
    }

    /**
     * 转 datetime，失败抛出异常
     */
    public static LocalDateTime toDateTimeOrThrow(String value) {
        return toDateTimeOrThrow(value, "无效的 datetime 值");
    }

    /**
     * 转 datetime，传入值为 null 或空字符串返回 null，否则抛出指定消息异常
     */
    public static LocalDateTime toDateTimeNullOrThrow(String value, String message) {
        if (isBlank(value))
            return null;
        return toDateTimeOrThrow(value, message);
    }

    /**
     * 转 datetime，传入值为 null 或空字符串返回 null，否则抛出异常
     */
    public static LocalDateTime toDateTimeNullOrThrow(String value) {
        if (isBlank(value))
            return null;
        return toDateTimeOrThrow(value);
    }

    // =================================================================================================================

    /**
     * 格式化 date
     */
    public static String formatDate(LocalDate value) {
        if (value != null)
            return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "";
    }

    /**
     * 转 date，失败返回 null
     */
    public static LocalDate toDateOrNull(String value) {
        if (isBlank(value))
            return null;

        // LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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

    // /**
    //  * 转 date，失败返回默认值
    //  */
    // public static LocalDate toDateOrDefault(String value, LocalDate defaultValue) {
    //     LocalDate d = toDateOrNull(value);
    //     if (d != null)
    //         return d;
    //     return defaultValue;
    // }

    /**
     * 转 date，失败抛出指定消息异常
     */
    public static LocalDate toDateOrThrow(String value, String message) {
        LocalDate d = toDateOrNull(value);
        if (d == null)
            throw new UtilityException(message);
        return d;
    }

    /**
     * 转 date，失败抛出异常
     */
    public static LocalDate toDateOrThrow(String value) {
        return toDateOrThrow(value, "无效的 date 值");
    }

    /**
     * 转 date，传入值为 null 或空字符串返回 null，否则抛出指定消息异常
     */
    public static LocalDate toDateNullOrThrow(String value, String message) {
        if (isBlank(value))
            return null;
        return toDateOrThrow(value, message);
    }

    /**
     * 转 date，传入值为 null 或空字符串返回 null，否则抛出异常
     */
    public static LocalDate toDateNullOrThrow(String value) {
        if (isBlank(value))
            return null;
        return toDateOrThrow(value);
    }

    // =================================================================================================================
    // time 精度统一为秒

    /**
     * 格式化 time，精度到秒
     */
    public static String formatTime(LocalTime value) {
        if (value != null)
            return value.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
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
        if (isBlank(value))
            return null;

        // try {
        //     // return LocalTime.parse(value.trim(), DateTimeFormatter.ofPattern("HH:mm:ss"));
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

    // /**
    //  * 转 time，精度到秒，失败返回默认值
    //  */
    // public static LocalTime toTimeOrDefault(String value, LocalTime defaultValue) {
    //     LocalTime t = toTimeOrNull(value);
    //     if (t != null)
    //         return t;
    //     if (defaultValue == null)
    //         return null;
    //     return defaultValue.truncatedTo(ChronoUnit.SECONDS);
    // }

    /**
     * 转 time，失败抛出指定消息异常
     */
    public static LocalTime toTimeOrThrow(String value, String message) {
        LocalTime t = toTimeOrNull(value);
        if (t == null)
            throw new UtilityException(message);
        return t;
    }

    /**
     * 转 time，失败抛出异常
     */
    public static LocalTime toTimeOrThrow(String value) {
        return toTimeOrThrow(value, "无效的 time 值");
    }

    /**
     * 转 time，传入值为 null 或空字符串返回 null，否则抛出指定消息异常
     */
    public static LocalTime toTimeNullOrThrow(String value, String message) {
        if (isBlank(value))
            return null;
        return toTimeOrThrow(value, message);
    }

    /**
     * 转 time，传入值为 null 或空字符串返回 null，否则抛出异常
     */
    public static LocalTime toTimeNullOrThrow(String value) {
        if (isBlank(value))
            return null;
        return toTimeOrThrow(value);
    }

    // =================================================================================================================

    /**
     * LinkedHashMap 构造器
     */
    public static class LinkedHashMapBuilder<K, V> {

        private final Map<K, V> map = new LinkedHashMap<>();

        /**
         * 返回自身以便链式调用
         */
        public LinkedHashMapBuilder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return Collections.unmodifiableMap(map);
        }
    }

}

