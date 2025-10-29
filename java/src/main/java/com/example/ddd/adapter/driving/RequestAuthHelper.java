package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserAuthService;
import com.example.ddd.application.UserDto;
import com.example.ddd.application.UserQueryHandler;
import com.example.ddd.utility.ValueUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 请求 Auth Helper
 */
@Component
public class RequestAuthHelper {

    private final UserAuthService userAuthService;
    private final UserQueryHandler userQueryHandler;

    private static RequestAuthHelper INSTANCE;

    public RequestAuthHelper(UserAuthService userAuthService, UserQueryHandler userQueryHandler) {
        this.userAuthService = userAuthService;
        this.userQueryHandler = userQueryHandler;
        INSTANCE = this;
    }

    /**
     * 获取静态实例
     */
    private static RequestAuthHelper getInstance() {
        if (INSTANCE == null)
            throw new ControllerException("RequestAuthHelper 静态实例未初始化");
        return INSTANCE;
    }

    /**
     * 获取登录用户Id，未登录返回空字符串
     */
    public static String getLoginUserId(HttpServletRequest request) {
        String accessToken = request.getHeader("Access-Token");
        //return SpringContextUtility.getBean(UserAuthService.class).getLoginUserId(accessToken);
        return getInstance().userAuthService.getLoginUserId(accessToken);
    }

    /**
     * 获取登录用户Id，未登录抛异常
     */
    public static String requireLoginUserId(HttpServletRequest request) {
        String userId = getLoginUserId(request);
        if (ValueUtility.isBlank(userId))
            throw new NotLoginException();
        return userId;
    }

    /**
     * 获取登录用户（管理员），失败抛异常
     */
    public static UserDto requireLoginUserAdmin(HttpServletRequest request) {
        String userId = requireLoginUserId(request);
        //UserDto userDto = SpringContextUtility.getBean(UserQueryHandler.class).queryById(userId);
        UserDto userDto = getInstance().userQueryHandler.queryById(userId);
        if (userDto == null || !userDto.isAdmin())
            throw new NotLoginException();
        return userDto;
    }
}
