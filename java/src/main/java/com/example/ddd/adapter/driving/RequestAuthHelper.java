package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserAuthService;
import com.example.ddd.application.UserDto;
import com.example.ddd.application.UserQueryHandler;
import com.example.ddd.utility.SpringContextUtility;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 请求 Auth Helper
 */
public class RequestAuthHelper {

    /**
     * 获取登录用户Id，未登录返回空字符串
     */
    public static String getLoginUserId(HttpServletRequest request) {
        String accessToken = request.getHeader("Access-Token");
        return SpringContextUtility.getBean(UserAuthService.class).getLoginUserId(accessToken);
    }

    /**
     * 获取登录用户Id，未登录抛异常
     */
    public static String requireLoginUserId(HttpServletRequest request) {
        String userId = getLoginUserId(request);
        if (StringUtils.isBlank(userId)) {
            throw new NotLoginException();
        }
        return userId;
    }

    /**
     * 获取登录用户（管理员），失败抛异常
     */
    public static UserDto requireLoginUserAdmin(HttpServletRequest request) {
        String userId = requireLoginUserId(request);
        UserDto userDto = SpringContextUtility.getBean(UserQueryHandler.class).queryById(userId);
        if (userDto == null || !userDto.isAdmin()) {
            throw new NotLoginException();
        }
        return userDto;
    }
}
