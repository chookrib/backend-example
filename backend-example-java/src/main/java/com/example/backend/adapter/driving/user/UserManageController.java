package com.example.backend.adapter.driving.user;

import com.example.backend.adapter.driving.RequestAuthHelper;
import com.example.backend.adapter.driving.RequestValueHelper;
import com.example.backend.adapter.driving.Result;
import com.example.backend.application.QuerySortBuilder;
import com.example.backend.application.QuerySortOption;
import com.example.backend.application.user.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理 Controller
 */
@RestController
public class UserManageController {

    private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

    private final UserQueryHandler userQueryHandler;
    private final UserManageService userManageService;
    private final UserAuthService userAuthService;

    public UserManageController(
            UserQueryHandler userQueryHandler,
            UserManageService userManageService,
            UserAuthService userAuthService) {
        this.userQueryHandler = userQueryHandler;
        this.userManageService = userManageService;
        this.userAuthService = userAuthService;
    }

    /**
     * 用户列表
     */
    @RequestMapping(value = "/api/admin/user/list", method = RequestMethod.POST)
    public Result userList(HttpServletRequest request, @RequestBody String requestBody) {
        RequestAuthHelper.requireLoginUserAdmin(request, userAuthService);

        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        int pageNum = RequestValueHelper.getRequestJsonIntOrDefault(requestJson, 1, "pageNum");
        int pageSize = RequestValueHelper.getRequestJsonIntOrDefault(requestJson, 1, "pageSize");

        //JsonNode criteriaJson = requestJson.path("criteria");
        UserQueryCriteria criteria = new UserQueryCriteria();
        //if (!criteriaJson.isMissingNode()) {
            //String keyword = criteriaJson.path("keyword").asText().trim();
            String keyword = RequestValueHelper.getRequestJsonStringTrimOrEmpty(requestJson,"criteria", "keyword");
            criteria.setKeyword(keyword);
        //}

        List<QuerySortOption> sortOptions = RequestValueHelper.getRequestJsonSortOptions(requestJson, "sort");

        int totalCount = userQueryHandler.queryCount(criteria);
        RequestValueHelper.Paging paging = RequestValueHelper.fixPaging(pageNum, pageSize, totalCount);
        List<UserDto> list = userQueryHandler.queryByPage(paging.pageNum(), paging.pageSize(), criteria, sortOptions);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("paging", Map.of(
                        "pageNum", paging.pageNum(),
                        "pageSize", paging.pageSize(),
                        "totalCount", paging.totalCount()
                )
        );
        return Result.okData(map);
    }

    /**
     * 用户详情
     */
    @RequestMapping(value = "/api/admin/user/get", method = RequestMethod.GET)
    public Result userGet(HttpServletRequest request) {
        RequestAuthHelper.requireLoginUserAdmin(request, userAuthService);

        String id = RequestValueHelper.getRequestParamStringTrimReq(request, "id");
        UserDto userDto = userQueryHandler.queryByIdReq(id);
        return Result.okData(Map.of(
                "detail", userDto
        ));
    }

    /**
     * 创建用户
     */
    @RequestMapping(value = "/api/admin/user/create", method = RequestMethod.POST)
    public Result userCreate(HttpServletRequest request, @RequestBody String requestBody) {
        RequestAuthHelper.requireLoginUserAdmin(request, userAuthService);

        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        String username = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "username");
        String password = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "password");
        String nickname = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "nickname");
        String mobile = RequestValueHelper.getRequestJsonStringTrimOrEmpty(requestJson, "mobile");

        String userId = userManageService.createUser(username, password, nickname, mobile);
        return Result.okData(Map.of(
                "id", userId
        ));
    }

    /**
     * 修改用户
     */
    @RequestMapping(value = "/api/admin/user/modify", method = RequestMethod.POST)
    public Result userModify(HttpServletRequest request, @RequestBody String requestBody) {
        RequestAuthHelper.requireLoginUserAdmin(request, userAuthService);

        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        String id = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "id");
        String username = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "username");
        String nickname = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "nickname");
        String mobile = RequestValueHelper.getRequestJsonStringTrimOrEmpty(requestJson, "mobile");

        userManageService.modifyUser(id, username, nickname, mobile);
        return Result.ok();
    }


    /**
     * 删除用户
     */
    @RequestMapping(value = "/api/admin/user/remove", method = RequestMethod.POST)
    public Result userRemove(HttpServletRequest request, @RequestBody String requestBody) {
        RequestAuthHelper.requireLoginUserAdmin(request, userAuthService);

        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        String id = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "id");

        userManageService.removeUser(id);
        return Result.ok();
    }
}
