package com.example.backend.application;

import com.fasterxml.uuid.Generators;

/**
 * Id 生成器
 */
public class IdGenerator {

    /**
     * Id 分隔符，指定一个 Id 中不存在的字符作为多个 Id 拼接分隔符
     */
    public static final String SEPARATOR = ",";

    /**
     * 生成唯一Id
     */
    public static String generateId() {
        return Generators.timeBasedEpochGenerator().generate().toString();   // UUID v7

        // return java.util.UUID.randomUUID().toString();   // UUID v4
        // return Generators.timeBasedGenerator().generate().toString();    // UUID v1
        // return Generators.timeBasedReorderedGenerator().generate().toString();   // UUID v6
    }
}
