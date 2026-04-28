package com.example.backend;

import com.example.backend.application.ApplicationConfig;
import com.example.backend.utility.ValueUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(Application.class, args);
        Properties props = getManifestProperties();
        logger.info("File-Name: {} Build-Time: {} Git-Commit-Id-Abbrev: {}",
                getFileName(),
                props.getProperty("Build-Time", ""),
                props.getProperty("Git-Commit-Id-Abbrev", ""));

        ApplicationConfig applicationConfig = applicationContext.getBean(ApplicationConfig.class);

        // 仅在开发环境打印配置，不记录日志
        if (applicationConfig.isAppEnvDev()) {
            // 打印 environment
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            System.out.println("environment:");
            environment.getPropertySources().forEach(propertySource -> {
                if (propertySource.getSource() instanceof java.util.Map) {
                    ((java.util.Map<?, ?>) propertySource.getSource()).forEach((k, v) -> {
                        System.out.println("    " + k + " = " + v);
                    });
                }
            });
        }

        // ConfigurableEnvironment environment = applicationContext.getEnvironment();
        // String appName = environment.getProperty("app.name", "");
        if (ValueUtility.isEmptyString(applicationConfig.getAppName())) {
            logger.warn("app.name 配置缺失");
        }

        logger.info("应用启动完成: {}", applicationConfig.getAppName());
    }

    /**
     * 取应用文件名
     */
    public static String getFileName() {
        return new ApplicationHome(applicationContext.getClass()).getSource().getName();
    }

    ///**
    // * 从MANIFEST.MF取应用打包时间
    // */
    //public static String getBuildTime() {
    //    try {
    //        InputStream inputStream = applicationContext.getClass().getClassLoader()
    //                .getResourceAsStream("META-INF/MANIFEST.MF");
    //
    //        if (inputStream == null) {
    //            logger.warn("获取 Build-Time 失败: META-INF/MANIFEST.MF 文件不存在");
    //            return "";
    //        }
    //
    //        Properties props = new Properties();
    //        props.load(inputStream);
    //
    //        for (String key : props.stringPropertyNames()) {
    //            if (key.equals("Build-Time")) {
    //                return props.getProperty(key);
    //            }
    //        }
    //
    //        logger.warn("获取 Build-Time 失败: META-INF/MANIFEST.MF 文件未包含 Build-Time");
    //        return "";
    //    } catch (Exception ex) {
    //        logger.warn("获取 Build-Time 失败: {}", ex.getMessage());
    //        return "";
    //    }
    //}

    /**
     * 从MANIFEST.MF取信息
     */
    public static Properties getManifestProperties() {
        Properties props = new Properties();
        try {
            InputStream inputStream = applicationContext.getClass().getClassLoader()
                    .getResourceAsStream("META-INF/MANIFEST.MF");

            if (inputStream == null) {
                logger.warn("读取 META-INF/MANIFEST.MF 失败: 文件不存在");
                return props;
            }

            props.load(inputStream);
            return props;

            //for (String key : props.stringPropertyNames()) {
            //    if (key.equals("Build-Time")) {
            //        return props.getProperty(key);
            //    }
            //}
        } catch (Exception ex) {
            logger.warn("读取 META-INF/MANIFEST.MF 失败: {}", ex.getMessage());
        }

        return props;
    }
}
