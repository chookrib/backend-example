package com.example.backend.adapter.driving.user;

import com.example.backend.adapter.driving.RequestIdentityResolver;
import com.example.backend.adapter.driving.RequestValueExtractor;
import com.example.backend.adapter.driving.Result;
import com.example.backend.application.user.UserDto;
import com.example.backend.application.user.UserQueryCriteria;
import com.example.backend.application.user.UserQueryHandler;
import com.example.backend.application.user.UserManageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理 Controller
 */
@RestController
public class UserManageController {

    private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

    private final RequestIdentityResolver requestIdentityResolver;
    private final UserQueryHandler userQueryHandler;
    private final UserManageService userManageService;

    public UserManageController(
            RequestIdentityResolver requestIdentityResolver,
            UserQueryHandler userQueryHandler,
            UserManageService userManageService
    ) {
        this.requestIdentityResolver = requestIdentityResolver;
        this.userQueryHandler = userQueryHandler;
        this.userManageService = userManageService;
    }

    /**
     * 用户列表
     */
    @RequestMapping(value = "/api/admin/user/list", method = RequestMethod.POST)
    public Result userList(@RequestBody String requestBody) {
        requestIdentityResolver.requireLoginAdminUserId();

        var requestJson = RequestValueExtractor.parseRequestJson(requestBody);
        int pageNum = RequestValueExtractor.getRequestJsonIntOrDefault(requestJson, 1, "pageNum");
        int pageSize = RequestValueExtractor.getRequestJsonIntOrDefault(requestJson, 1, "pageSize");

        // JsonNode criteriaJson = requestJson.path("criteria");
        UserQueryCriteria criteria = new UserQueryCriteria();
        // if (!criteriaJson.isMissingNode()) {
        // String keyword = criteriaJson.path("keyword").asText().trim();
        String keyword = RequestValueExtractor.getRequestJsonStringTrimOrEmpty(requestJson, "criteria", "keyword");
        criteria.setKeyword(keyword);
        //}

        LinkedHashMap<String, String> sortOptions = RequestValueExtractor.getRequestJsonSortOptions(requestJson, "sort");

        int totalCount = userQueryHandler.queryCount(criteria);
        RequestValueExtractor.Paging paging = RequestValueExtractor.normalizePaging(pageNum, pageSize, totalCount);
        List<UserDto> list = userQueryHandler.queryPage(paging.pageNum(), paging.pageSize(), criteria, sortOptions);
        return Result.okData(Map.of(
                "list", list,
                "paging", Map.of(
                        "pageNum", paging.pageNum(),
                        "pageSize", paging.pageSize(),
                        "totalCount", paging.totalCount()
                )
        ));
    }

    /**
     * 用户详情
     */
    @RequestMapping(value = "/api/admin/user/get", method = RequestMethod.GET)
    public Result userGet(HttpServletRequest request) {
        requestIdentityResolver.requireLoginAdminUserId();

        String id = RequestValueExtractor.getRequestParamStringTrimOrThrow(request, "id");
        UserDto userDto = userQueryHandler.queryByIdOrThrow(id);
        return Result.okData(Map.of(
                "detail", userDto
        ));
    }

    /**
     * 创建用户
     */
    @RequestMapping(value = "/api/admin/user/create", method = RequestMethod.POST)
    public Result userCreate(@RequestBody String requestBody) {
        requestIdentityResolver.requireLoginAdminUserId();

        var requestJson = RequestValueExtractor.parseRequestJson(requestBody);
        String username = RequestValueExtractor.getRequestJsonStringTrimOrThrow(requestJson, "username");
        String password = RequestValueExtractor.getRequestJsonStringTrimOrThrow(requestJson, "password");
        String nickname = RequestValueExtractor.getRequestJsonStringTrimOrThrow(requestJson, "nickname");
        String mobile = RequestValueExtractor.getRequestJsonStringTrimOrEmpty(requestJson, "mobile");

        String userId = userManageService.createUser(username, password, nickname, mobile);
        return Result.okData(Map.of(
                "id", userId
        ));
    }

    /**
     * 修改用户
     */
    @RequestMapping(value = "/api/admin/user/modify", method = RequestMethod.POST)
    public Result userModify(@RequestBody String requestBody) {
        requestIdentityResolver.requireLoginAdminUserId();

        var requestJson = RequestValueExtractor.parseRequestJson(requestBody);
        String id = RequestValueExtractor.getRequestJsonStringTrimOrThrow(requestJson, "id");
        String username = RequestValueExtractor.getRequestJsonStringTrimOrThrow(requestJson, "username");
        String nickname = RequestValueExtractor.getRequestJsonStringTrimOrThrow(requestJson, "nickname");
        String mobile = RequestValueExtractor.getRequestJsonStringTrimOrEmpty(requestJson, "mobile");

        userManageService.modifyUser(id, username, nickname, mobile);
        return Result.ok();
    }

    /**
     * 删除用户
     */
    @RequestMapping(value = "/api/admin/user/remove", method = RequestMethod.POST)
    public Result userRemove(@RequestBody String requestBody) {
        requestIdentityResolver.requireLoginAdminUserId();

        var requestJson = RequestValueExtractor.parseRequestJson(requestBody);
        String id = RequestValueExtractor.getRequestJsonStringTrimOrThrow(requestJson, "id");

        userManageService.removeUser(id);
        return Result.ok();
    }
}
