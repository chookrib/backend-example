package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserDto;
import com.example.ddd.application.UserManageService;
import com.example.ddd.application.UserQueryCriteria;
import com.example.ddd.application.UserQueryHandler;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理 Controller
 */
@RestController
public class UserManageController {

    private final UserQueryHandler userQueryHandler;
    private final UserManageService userManageService;

    public UserManageController(UserQueryHandler userQueryHandler, UserManageService userManageService) {
        this.userQueryHandler = userQueryHandler;
        this.userManageService = userManageService;
    }

    /**
     * 用户列表
     */
    @RequestMapping(value = "/api/admin/user/list", method = RequestMethod.POST)
    public Result userList(HttpServletRequest request, @RequestBody String requestBody) {
        RequestHelper.requireLoginUserAdmin(request);

        JsonNode requestJson = RequestHelper.toJson(requestBody);
        int pageNum = requestJson.path("pageNum").asInt(1);
        int pageSize = requestJson.path("pageSize").asInt(1);

        JsonNode criteriaJson = requestJson.path("criteria");
        UserQueryCriteria criteria = new UserQueryCriteria();
        if (!criteriaJson.isMissingNode()) {
            String keyword = criteriaJson.path("keyword").asText().trim();
            criteria.setKeyword(keyword);
        }

        int totalCount = this.userQueryHandler.queryCount(criteria);
        PagingValidator paging = PagingValidator.validation(pageNum, pageSize, totalCount);
        List<UserDto> list = this.userQueryHandler.queryByPage(paging.getPageNum(), paging.getPageSize(), criteria);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("paging", Map.of(
                        "pageNum", paging.getPageNum(),
                        "pageSize", paging.getPageSize(),
                        "totalCount", paging.getTotalCount()
                )
        );
        return Result.okData(map);
    }

    /**
     * 用户详情
     */
    @RequestMapping(value = "/api/admin/user/get", method = RequestMethod.GET)
    public Result userGet(HttpServletRequest request, @RequestParam String id) {
        RequestHelper.requireLoginUserAdmin(request);

        UserDto userDto = this.userQueryHandler.queryByIdReq(id);
        return Result.okData(Map.of(
                "detail", userDto
        ));
    }

    /**
     * 创建用户
     */
    @RequestMapping(value = "/api/admin/user/create", method = RequestMethod.POST)
    public Result userCreate(HttpServletRequest request, @RequestBody String requestBody) {
        RequestHelper.requireLoginUserAdmin(request);

        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String username = requestJson.path("username").asText().trim();
        String password = requestJson.path("password").asText().trim();
        String nickname = requestJson.path("nickname").asText().trim();
        String mobile = requestJson.path("mobile").asText().trim();

        String userId = this.userManageService.createUser(username, password, nickname, mobile);
        return Result.okData(Map.of(
                "id", userId
        ));
    }

    /**
     * 修改用户
     */
    @RequestMapping(value = "/api/admin/user/modify", method = RequestMethod.POST)
    public Result userModify(HttpServletRequest request, @RequestBody String requestBody) {
        RequestHelper.requireLoginUserAdmin(request);

        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String id = requestJson.path("id").asText().trim();
        String username = requestJson.path("username").asText().trim();
        String nickname = requestJson.path("nickname").asText().trim();
        String mobile = requestJson.path("mobile").asText().trim();

        this.userManageService.modifyUser(id, username, nickname, mobile);
        return Result.ok();
    }


    /**
     * 删除用户
     */
    @RequestMapping(value = "/api/admin/user/remove", method = RequestMethod.POST)
    public Result userRemove(HttpServletRequest request, @RequestBody String requestBody) {
        RequestHelper.requireLoginUserAdmin(request);

        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String id = requestJson.path("id").asText().trim();

        this.userManageService.removeUser(id);
        return Result.ok();
    }
}
