package com.example.backend.adapter.driving;

import com.example.backend.Accessor;
import com.example.backend.application.UserAuthService;
import com.example.backend.application.UserDto;
import com.example.backend.application.UserQueryHandler;
import com.example.backend.utility.ValueUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 请求 Auth Helper
 */
@Component
public class RequestAuthHelper {

    private final UserAuthService userAuthService;
    private final UserQueryHandler userQueryHandler;

    public RequestAuthHelper(UserAuthService userAuthService, UserQueryHandler userQueryHandler) {
        this.userAuthService = userAuthService;
        this.userQueryHandler = userQueryHandler;
    }

    /**
     * 获取登录用户Id，未登录返回空字符串
     */
    public static String getLoginUserId(HttpServletRequest request) {
        String accessToken = request.getHeader("Access-Token");
        //return ApplicationContextUtility.getBean(UserAuthService.class).getLoginUserId(accessToken);
        return Accessor.getBean(UserAuthService.class).getLoginUserId(accessToken);
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
        //UserDto userDto = ApplicationContextUtility.getBean(UserQueryHandler.class).queryById(userId);
        UserDto userDto = Accessor.getBean(UserQueryHandler.class).queryById(userId);
        if (userDto == null || !userDto.isAdmin())
            throw new NotLoginException();
        return userDto;
    }
}
