package com.example.backend.application.test;

/**
 * 循环依赖测试类1
 */
//@Component
public class TestIocService1 {

     private final TestIocService2 service2;

     public TestIocService1(TestIocService2 service2) {
          this.service2 = service2;
     }
}
