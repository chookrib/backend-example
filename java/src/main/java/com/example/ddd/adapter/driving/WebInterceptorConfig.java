//package com.example.ddd.adapter.driving;
//
//import com.example.ddd.application.UserProfileService;
//import io.micrometer.common.util.StringUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.lang.NonNull;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * Web拦截器配置
// */
//@Configuration
//public class WebInterceptorConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//
//        //拦截器
//        InterceptorRegistration globalInterceptor = registry.addInterceptor(getGlobalHandlerInterceptor());
//
//        //排除
//        //globalInterceptor.excludePathPatterns("/img/**");
//        //globalInterceptor.excludePathPatterns("/js/**");
//        //globalInterceptor.excludePathPatterns("/css/**");
//        //globalInterceptor.excludePathPatterns("/**/*.html");
//
//        //排除所有地址
//        //globalInterceptor.excludePathPatterns("/**");
//
//        // 需要特别注意拦截配置，以防遗漏
//
//        //排除地址
//        globalInterceptor.excludePathPatterns("/api/register");
//        globalInterceptor.excludePathPatterns("/api/login");
//
//        //拦截地址
//        globalInterceptor.addPathPatterns("/api/**");
//    }
//
//    @Bean
//    public AccessTokenInterceptor getGlobalHandlerInterceptor() {
//        return new AccessTokenInterceptor();
//    }
//
//    ///**
//    // * 跨域配置
//    // * 此方式预检请求OPTIONS会先被拦截器拦截，allowedMethods改为*也有同样问题
//    // * 使用@CrossOrigin注解也有同样问题
//    // * 改为使用CorsFilter实现
//    // */
//    //@Override
//    //public void addCorsMappings(CorsRegistry registry) {
//    //    registry.addMapping("/**")
//    //            .allowedOrigins("*")
//    //            //.allowedMethods("*")
//    //            .allowedMethods("GET", "POST")
//    //            ;
//    //}
//
//    /**
//     * 认证拦截器
//     */
//    public class AccessTokenInterceptor implements HandlerInterceptor {
//
//        @Autowired
//        private UserProfileService userProfileService;
//
//        @Override
//        public boolean preHandle(
//                HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
//
//            String accessToken = request.getHeader("Access-Token");
//
//            String userId = userProfileService.decodeAccessToken(accessToken);
//            if (StringUtils.isBlank(userId)) {
//                throw new NotLoginException();
//            }
//            request.setAttribute("userId", userId);
//            //request.getAttribute("userId").toString();
//
//            return true;
//        }
//    }
//}
