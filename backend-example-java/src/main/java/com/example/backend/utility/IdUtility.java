package com.example.backend.utility;

import com.fasterxml.uuid.Generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Id Utility
 */
public final class IdUtility {

    /**
     * 生成 UUID v4
     */
    public static String generateUUIDv4(){
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * 生成 UUID v7
     */
    public static String generateUUIDv7() {
        return Generators.timeBasedEpochGenerator().generate().toString();   // UUID v7

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
    public static String wrapJoin(List<String> ids) {
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
    public static String wrap(String id) {
        if(ValueUtility.isEmptyString(id))
            throw new UtilityException("不能包裹一个空的 Id");
        return DELIMITER + id + DELIMITER;
    }

    /**
     * 拆分 Id 字符串
     */
    public static List<String> split(String ids) {
        if(ValueUtility.isEmptyString(ids))
            return new ArrayList<>();
        return Arrays.stream(ids.split(DELIMITER))
                // .map(String::trim)
                .filter(s -> !ValueUtility.isEmptyString(s))
                .collect(Collectors.toList());
    }
}
