package com.example.backend.adapter.driving;

import com.example.backend.application.user.UserAuthService;
import com.example.backend.application.user.UserDto;
import com.example.backend.application.user.UserQueryHandler;
import com.example.backend.utility.ValueUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求身份 Resolver
 */
@Component
public class RequestIdentityResolver {

    private final UserAuthService userAuthService;
    private final UserQueryHandler userQueryHandler;

    public RequestIdentityResolver(
            UserAuthService userAuthService,
            UserQueryHandler userQueryHandler) {
        this.userAuthService = userAuthService;
        this.userQueryHandler = userQueryHandler;
    }

    /**
     * 获取 request
     */
    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    /**
     * 获取登录用户 Id，未登录返回空字符串
     */
    public String getLoginUserId() {
        String accessToken = getRequest().getHeader("Access-Token");
        return userAuthService.getLoginUserId(accessToken);
    }

    /**
     * 获取登录用户 Id，未登录抛出异常
     */
    public String requireLoginUserId() {
        String accessToken = getRequest().getHeader("Access-Token");
        String userId = userAuthService.getLoginUserId(accessToken);
        if (ValueUtility.isBlank(userId))
            throw new NotLoginException();
        return userId;
    }

    /**
     * 判断管理员权限
     */
    public void checkAdminPermission() {
        String userId = requireLoginUserId();
        UserDto user = userQueryHandler.queryById(userId);
        if (user == null || !user.getIsAdmin())
            throw new NotLoginException();
    }
}
