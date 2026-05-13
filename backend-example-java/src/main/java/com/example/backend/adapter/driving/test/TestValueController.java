package com.example.backend.adapter.driving.test;

import com.example.backend.adapter.driving.Result;
import com.example.backend.utility.ValueUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Value 测试 Controller
 */
@RestController
public class TestValueController {

    private static final Logger logger = LoggerFactory.getLogger(TestValueController.class);

    /**
     * 测试日期时间转换
     */
    @RequestMapping(value = "/api/test/value/to-datetime", method = RequestMethod.GET)
    public Result testValueToDatetime() {
        HashMap<String, Object> dateTimeMap = new LinkedHashMap<>();

        dateTimeMap.put("yyyy", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.S", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.1")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.12")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.123")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.1234")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.12345")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.123456")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.1234567")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.12345678")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.123456789")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("2026-01-02 03:04:05.0123456789")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.S", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.1")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.12")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.123")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.1234")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.12345")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.123456")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SSSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.1234567")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SSSSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.12345678")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SSSSSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.123456789")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));
        dateTimeMap.put("HH:mm:ss.SSSSSSSSSS", Optional.ofNullable(ValueUtility.toDateTimeOrNull("03:04:05.0123456789")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS"))).orElse(null));

        HashMap<String, Object> dateMap = new LinkedHashMap<>();
        dateMap.put("yyyy", ValueUtility.toDateOrNull("2026"));
        dateMap.put("yyyy-MM", ValueUtility.toDateOrNull("2026-01"));
        dateMap.put("yyyy-MM-dd", ValueUtility.toDateOrNull("2026-01-02"));
        dateMap.put("yyyy-MM-dd HH", ValueUtility.toDateOrNull("2026-01-02 03"));
        dateMap.put("yyyy-MM-dd HH:mm", ValueUtility.toDateOrNull("2026-01-02 03:04"));
        dateMap.put("yyyy-MM-dd HH:mm:ss", ValueUtility.toDateOrNull("2026-01-02 03:04:05"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.S", ValueUtility.toDateOrNull("2026-01-02 03:04:05.1"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.12"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SSS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.123"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SSSS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.1234"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SSSSS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.12345"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SSSSSS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.123456"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.1234567"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.12345678"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSSS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.123456789"));
        dateMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSSSS", ValueUtility.toDateOrNull("2026-01-02 03:04:05.0123456789"));
        dateMap.put("HH", ValueUtility.toDateOrNull("03"));
        dateMap.put("HH:mm", ValueUtility.toDateOrNull("03:04"));
        dateMap.put("HH:mm:ss", ValueUtility.toDateOrNull("03:04:05"));
        dateMap.put("HH:mm:ss.S", ValueUtility.toDateOrNull("03:04:05.1"));
        dateMap.put("HH:mm:ss.SS", ValueUtility.toDateOrNull("03:04:05.12"));
        dateMap.put("HH:mm:ss.SSS", ValueUtility.toDateOrNull("03:04:05.123"));
        dateMap.put("HH:mm:ss.SSSS", ValueUtility.toDateOrNull("03:04:05.1234"));
        dateMap.put("HH:mm:ss.SSSSS", ValueUtility.toDateOrNull("03:04:05.12345"));
        dateMap.put("HH:mm:ss.SSSSSS", ValueUtility.toDateOrNull("03:04:05.123456"));
        dateMap.put("HH:mm:ss.SSSSSSS", ValueUtility.toDateOrNull("03:04:05.1234567"));
        dateMap.put("HH:mm:ss.SSSSSSSS", ValueUtility.toDateOrNull("03:04:05.12345678"));
        dateMap.put("HH:mm:ss.SSSSSSSSS", ValueUtility.toDateOrNull("03:04:05.123456789"));
        dateMap.put("HH:mm:ss.SSSSSSSSSS", ValueUtility.toDateOrNull("03:04:05.0123456789"));

        HashMap<String, Object> timeMap = new LinkedHashMap<>();
        timeMap.put("yyyy", Optional.ofNullable(ValueUtility.toTimeOrNull("2026")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.S", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.1")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.12")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SSS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.123")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.1234")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.12345")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.123456")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.1234567")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.12345678")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.123456789")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("yyyy-MM-dd HH:mm:ss.SSSSSSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("2026-01-02 03:04:05.0123456789")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH", Optional.ofNullable(ValueUtility.toTimeOrNull("03")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.S", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.1")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.12")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SSS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.123")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.1234")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.12345")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.123456")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SSSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.1234567")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SSSSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.12345678")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SSSSSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.123456789")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));
        timeMap.put("HH:mm:ss.SSSSSSSSSS", Optional.ofNullable(ValueUtility.toTimeOrNull("03:04:05.0123456789")).map(e -> e.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"))).orElse(null));

        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("toDateTime", dateTimeMap);
        map.put("toDate", dateMap);
        map.put("toTime", timeMap);

        return Result.okData(map);
    }
}
