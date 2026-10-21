package com.example.backend.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Application Config
 */
@Component
public class ApplicationConfig {

    private final ApplicationContext applicationContext;
    private final String appName;
    private final String appEnv;

    public ApplicationConfig(
            ApplicationContext applicationContext,
            @Value("${app.name:}") String appName,
            @Value("${app.env:}") String appEnv) {
        this.applicationContext = applicationContext;
        this.appName = appName;
        this.appEnv = appEnv;
    }

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
    //         throw new ApplicationException("没有提供 ApplicationContext");
    //     try {
    //         return applicationContext.getBean(clazz);
    //     } catch (org.springframework.beans.factory.NoSuchBeanDefinitionException ex) {
    //         throw new ApplicationException(String.format("ApplicationContext 中没有注册 %s", clazz), ex);
    //     }
    // }
}
