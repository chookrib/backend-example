package com.example.ddd.adapter.driving;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 跨域配置
 */
@Configuration
public class CorsFilterConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        //允许来源
        //SpringBoot旧版本有效，新版本无效
        //config.addAllowedOrigin("*");
        //SpringBoot新版本有效
        List<String> allowOriginPatterns = new ArrayList<>();
        allowOriginPatterns.add("*");
        config.setAllowedOriginPatterns(allowOriginPatterns);

        //允许请求方式
        config.addAllowedMethod("*");
        //允许请求头
        config.addAllowedHeader("*");
        //允许获取凭证
        //config.setAllowCredentials(true);
        //暴露响应头
        //config.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    //@Bean
    //public FilterRegistrationBean<CorsFilter> corsFilter() {
    //    CorsConfiguration config = new CorsConfiguration();
    //    config.addAllowedOrigin("*");
    //    config.addAllowedMethod("*");
    //    config.setAllowCredentials(true);
    //    config.addAllowedHeader("*");
    //    config.addExposedHeader("*");
    //    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //    source.registerCorsConfiguration("/**", config);
    //    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    //    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    //    return bean;
    //}
}
