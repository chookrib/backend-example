package com.example.backend.application;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

/**
 * 应用配置
 */
@Component
public class ApplicationConfig {

    // private final ApplicationContext applicationContext;

    @Value("${app.name:}")
    private String appName;

    @Value("${app.env:}")
    private String appEnv;

    // public ApplicationConfig(ApplicationContext applicationContext) {
    //     this.applicationContext = applicationContext;
    // }

    /**
     * 应用名称
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 应用运行环境
     */
    public String getAppEnv() {
        return appEnv;
    }

    /**
     * 应用运行环境是否是开发环境
     */
    public boolean isAppEnvDev() {
        return appEnv.equalsIgnoreCase("dev");
    }

    // /**
    //  * 获取 Spring 容器中的 Bean
    //  */
    // public <T> T getBean(Class<T> clazz) {
    //     if (applicationContext == null)
    //         throw new RuntimeException("没有提供 ApplicationContext");
    //     try {
    //         return applicationContext.getBean(clazz);
    //     } catch (NoSuchBeanDefinitionException ex) {
    //         throw new RuntimeException(String.format("ApplicationContext 中没有注册 %s", clazz), ex);
    //     }
    // }
}
