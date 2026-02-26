package com.example.backend;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * 访问器
 */
public class Accessor {

    /**
     * 应用名称
     */
    public static String appName = "";

    /**
     * 应用是否是开发环境
     */
    public static boolean appEnvIsDev = false;

    /**
     * Spring 应用上下文
     */
    public static ApplicationContext appContext;

    /**
     * 获取 Spring 容器中的 Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        if (appContext == null)
            throw new RuntimeException("没有为 Accessor 提供 appContext");
        try {
            return appContext.getBean(clazz);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new RuntimeException(String.format("Accessor appContext 中没有注册 %s", clazz), ex);
        }
    }
}
