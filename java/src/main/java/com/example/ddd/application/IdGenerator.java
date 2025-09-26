package com.example.ddd.application;

import com.fasterxml.uuid.Generators;

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
}
