package com.example.backend.application.test;

import org.springframework.stereotype.Component;

/**
 * IoC测试类3
 */
@Component
public class TestIocClass3 {

    /**
     * 不会引起循环依赖的构造函数
     */
    public TestIocClass3() {
    }

    //private final TestIocClass1 class1;
    //
    ///**
    // * 会引起循环依赖的构造函数
    // */
    //public TestIocClass3(TestIocClass1 class1) {
    //    this.class1 = class1;
    //}
}
