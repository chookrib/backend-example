package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserProfileService;
import com.example.ddd.domain.User;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 请求处理器
 */
@Component
public class RequestHandler {

    private final UserProfileService userProfileService;

    public RequestHandler(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * 获取登录用户Id，未登录返回空字符串
     */
    public String getLoginUserId(HttpServletRequest request) {
        String accessToken = request.getHeader("Access-Token");
        return userProfileService.decodeAccessToken(accessToken);
    }

    /**
     * 获取登录用户Id，未登录抛异常
     */
    public String requireLoginUserId(HttpServletRequest request) {
        String userId = getLoginUserId(request);
        if(StringUtils.isBlank(userId)) {
            throw new NotLoginException();
        }
        return userId;
    }

    /**
     * 修正分页索引，当超过总数时，返回最后一页索引
     */
    public static int fixPageIndex(int pageIndex, int pageSize, int totalCount) {
        if(pageSize * (pageIndex - 1) >= totalCount)
            return Math.max(1, (totalCount + pageSize - 1));
        return pageIndex;
    }
}
