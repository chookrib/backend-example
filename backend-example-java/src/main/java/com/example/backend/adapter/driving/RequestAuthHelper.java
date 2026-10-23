package com.example.backend.adapter.driving;

import com.example.backend.application.user.UserAuthService;
import com.example.backend.domain.user.User;
import com.example.backend.utility.ValueUtility;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 请求 Auth Helper
 */
public class RequestAuthHelper {

    /**
     * 获取登录用户 Id，未登录返回空字符串
     */
    public static String getLoginUserId(HttpServletRequest request, UserAuthService userAuthService) {
        String accessToken = request.getHeader("Access-Token");
        return userAuthService.getLoginUserId(accessToken);
    }

    /**
     * 获取登录用户 Id，未登录抛异常
     */
    public static String requireLoginUserId(HttpServletRequest request, UserAuthService userAuthService) {
        String accessToken = request.getHeader("Access-Token");
        String userId = userAuthService.getLoginUserId(accessToken);
        if (ValueUtility.isEmptyString(userId))
            throw new NotLoginException();
        return userId;
    }

    /**
     * 获取登录管理员用户 Id，失败抛异常
     */
    public static String requireLoginAdminUserId(HttpServletRequest request, UserAuthService userAuthService) {
        String accessToken = request.getHeader("Access-Token");
        User user = userAuthService.getLoginUser(accessToken);
        if (user == null || !user.isAdmin())
            throw new NotLoginException();
        return user.getId();
    }
}
