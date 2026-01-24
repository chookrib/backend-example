//package com.example.backend.utility;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//
///**
// * Spring 应用上下文 Utility
// */
//@Component
//public class ApplicationContextUtility implements ApplicationContextAware {
//
//    private static ApplicationContext context;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) {
//        ApplicationContextUtility.context = applicationContext;
//    }
//
//    /**
//     * 获取 Spring 容器中的 Bean，反模式，请谨慎使用
//     */
//    public static <T> T getBean(Class<T> clazz) {
//        return context.getBean(clazz);
//    }
//}