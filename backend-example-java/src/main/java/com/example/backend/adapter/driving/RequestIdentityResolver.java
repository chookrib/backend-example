package com.example.backend.adapter.driving;

import com.example.backend.application.user.UserAuthService;
import com.example.backend.domain.user.User;
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

    public RequestIdentityResolver(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
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
     * 获取登录用户 Id，未登录抛异常
     */
    public String requireLoginUserId() {
        String accessToken = getRequest().getHeader("Access-Token");
        String userId = userAuthService.getLoginUserId(accessToken);
        if (ValueUtility.isEmptyString(userId))
            throw new NotLoginException();
        return userId;
    }

    /**
     * 获取登录管理员用户 Id，失败抛异常
     */
    public String requireLoginAdminUserId() {
        String accessToken = getRequest().getHeader("Access-Token");
        User user = userAuthService.getLoginUser(accessToken);
        if (user == null || !user.getIsAdmin())
            throw new NotLoginException();
        return user.getId();
    }
}
