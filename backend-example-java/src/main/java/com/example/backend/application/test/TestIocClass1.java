package com.example.backend.application.test;

import org.springframework.stereotype.Component;

/**
 * IoC测试类1
 */
@Component
public class TestIocClass1 {

    private final TestIocClass2 service2;

    public TestIocClass1(TestIocClass2 service2) {
        this.service2 = service2;
    }
}
