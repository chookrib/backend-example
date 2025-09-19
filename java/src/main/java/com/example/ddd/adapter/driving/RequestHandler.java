package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserProfileService;
import com.example.ddd.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * 请求处理器
 */
@Service
public class RequestHandler {

    private final UserProfileService userProfileService;

    public RequestHandler(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * 获取登录用户Id，未登录返回空字符串
     */
    public String getLoginUser(HttpServletRequest request) {
        String accessToken = request.getHeader("Access-Token");
        try {
            return userProfileService.decodeUserId(accessToken);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取登录用户Id，未登录抛异常
     */
    public String getLoginUserIdReq(HttpServletRequest request) {
        String accessToken = request.getHeader("Access-Token");
        try {
            return userProfileService.decodeUserId(accessToken);
        } catch (Exception e) {
            throw new NotLoginException();
        }
    }
}
