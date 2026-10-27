package com.example.backend.adapter.driving;

import com.example.backend.application.user.UserAuthService;
import com.example.backend.utility.ValueUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 拦截器配置
 */
@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // registry.addInterceptor(getGlobalInterceptor())
        //         .addPathPatterns("/**")
        //         .excludePathPatterns("/img/**")
        //         .excludePathPatterns("/js/**")
        //         .excludePathPatterns("/css/**")
        //         .excludePathPatterns("/**/*.html")
        // ;

        registry.addInterceptor(getAccessTokenInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/register")
                .excludePathPatterns("/api/user/login")
        ;
    }

    @Bean
    public AccessTokenInterceptor getAccessTokenInterceptor() {
        return new AccessTokenInterceptor();
    }

    /**
     * AccessToken 验证拦截器
     */
    public class AccessTokenInterceptor implements HandlerInterceptor {

        @Autowired
        private UserAuthService userAuthService;

        @Override
        public boolean preHandle(
                HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler
        ) {
            String accessToken = request.getHeader("Access-Token");

            String userId = userAuthService.getLoginUserId(accessToken);
            if (ValueUtility.isEmptyString(userId)) {
                throw new NotLoginException();
            }
            request.setAttribute("userId", userId);
            // request.getAttribute("userId").toString();

            return true;
        }
    }
}
