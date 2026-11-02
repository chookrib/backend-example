package com.example.backend.application;

import com.example.backend.utility.ValueUtility;
import com.fasterxml.uuid.Generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Id 生成器
 */
public class IdGenerator {

    /**
     * 生成唯一Id
     */
    public static String generateId() {
        return Generators.timeBasedEpochGenerator().generate().toString();   // UUID v7

        // return java.util.UUID.randomUUID().toString();   // UUID v4
        // return Generators.timeBasedGenerator().generate().toString();    // UUID v1
        // return Generators.timeBasedReorderedGenerator().generate().toString();   // UUID v6
    }

    /**
     * Id 界定符，指定一个 Id 中不存在的字符作为多个 Id 拼接界定符
     */
    public static final String DELIMITER = ",";

    /**
     * 拼接 Id 字符串
     */
    public static String joinWithDelimiter(List<String> ids) {
        if(ids == null || ids.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        for(String id : ids){
            if (!ValueUtility.isEmptyString(id)) {
                sb.append(id).append(DELIMITER);
            }
        }
        if(!sb.isEmpty())
            return DELIMITER + sb.toString();
        else
            return "";
    }

    /**
     * 包裹 Id 字符串
     */
    public static String wrapWithDelimiter(String id) {
        if(ValueUtility.isEmptyString(id))
            throw new ApplicationException("不能包裹一个空的 Id");
        return DELIMITER + id + DELIMITER;
    }

    /**
     * 拆分 Id 字符串
     */
    public static List<String> splitWithDelimiter(String ids) {
        if(ValueUtility.isEmptyString(ids))
            return new ArrayList<>();
        return Arrays.stream(ids.split(DELIMITER))
                .map(String::trim)                      // 去除每个元素的首尾空白
                .filter(s -> !s.isEmpty())      // 过滤空字符串
                .collect(Collectors.toList());
    }
}
