package com.example.backend.application.test;

/**
 * 循环依赖测试类2
 */
//@Component
public class TestIocService2 {

    private final TestIocService1 service1;

    public TestIocService2(TestIocService1 service1) {
        this.service1 = service1;
    }
}
