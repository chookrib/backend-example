package com.example.backend.adapter.driving;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsFilterConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许携带 Cookie
        // config.setAllowCredentials(true);

        // 允许来源
        config.addAllowedOriginPattern("*");

        // 允许请求方式
        config.addAllowedMethod("*");

        // 允许请求头
        config.addAllowedHeader("*");

        // 暴露响应头
        // config.addExposedHeader("*");

        // 预检请求的缓存时间（秒）
        // config.setMaxAge(1800L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
