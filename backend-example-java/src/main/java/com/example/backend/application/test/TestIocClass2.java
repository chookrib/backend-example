package com.example.backend.application.test;

import org.springframework.stereotype.Component;

/**
 * IoC测试类2
 */
@Component
public class TestIocClass2 {

    private final TestIocClass3 class3;

    public TestIocClass2(TestIocClass3 class3) {
        this.class3 = class3;
    }
}
