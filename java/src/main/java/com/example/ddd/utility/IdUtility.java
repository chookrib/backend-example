package com.example.ddd.utility;

import com.fasterxml.uuid.Generators;

/**
 * Id工具类
 */
public class IdUtility {

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
