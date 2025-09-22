package com.example.ddd.adapter.driving;

import com.example.ddd.application.*;
import com.example.ddd.utility.JacksonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理Controller
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

        JsonNode json = JacksonUtility.readTree(requestBody);
        int pageNum = json.path("pageNum").asInt(1);
        int pageSize = json.path("pageSize").asInt(1);

        JsonNode criteriaJson = json.path("criteria");
        UserQueryCriteria criteria = new UserQueryCriteria();
        if (!criteriaJson.isMissingNode()) {
            String keyword = criteriaJson.path("keyword").asText();
            criteria.setKeyword(keyword);
        }

        int totalCount = userQueryHandler.queryCount(criteria);
        PageInfoValidator pageInfo = PageInfoValidator.validation(pageNum, pageSize, totalCount);
        List<UserDto> list = userQueryHandler.queryByPage(pageInfo.getPageNum(), pageInfo.getPageSize(), criteria);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("page", Map.of(
                "pageNum", pageInfo.getPageNum(),
                "pageSize", pageInfo.getPageSize(),
                "totalCount", pageInfo.getTotalCount()
                )
        );
        return Result.successData(map);
    }

    /**
     * 用户详情
     */
    @RequestMapping(value = "/api/admin/user/get", method = RequestMethod.GET)
    public Result userGet(HttpServletRequest request, @RequestParam String id) {
        RequestHelper.requireLoginUserAdmin(request);

        UserDto dto = userQueryHandler.queryById(id);
        return Result.successData(dto);
    }

    /**
     * 修改用户
     */
    @RequestMapping(value = "/api/admin/user/create", method = RequestMethod.POST)
    public Result userCreate(HttpServletRequest request, @RequestBody String requestBody) {
        RequestHelper.requireLoginUserAdmin(request);

        JsonNode json = JacksonUtility.readTree(requestBody);
        String username = json.path("username").asText();
        String password = json.path("password").asText();
        String nickname = json.path("nickname").asText();
        String mobile = json.path("mobile").asText();

        String userId = userManageService.createUser(username, password, nickname, mobile);
        return Result.successData(userId);
    }

    /**
     * 修改用户
     */
    @RequestMapping(value = "/api/admin/user/modify", method = RequestMethod.POST)
    public Result userModify(HttpServletRequest request, @RequestBody String requestBody) {
        RequestHelper.requireLoginUserAdmin(request);

        JsonNode json = JacksonUtility.readTree(requestBody);
        String id = json.path("id").asText();
        String username = json.path("username").asText();
        String nickname = json.path("nickname").asText();
        String mobile = json.path("mobile").asText();

        userManageService.modifyUser(id, username, nickname, mobile);
        return Result.success();
    }


    /**
     * 删除用户
     */
    @RequestMapping(value = "/api/admin/user/remove", method = RequestMethod.POST)
    public Result userRemove(HttpServletRequest request, @RequestBody String requestBody) {
        RequestHelper.requireLoginUserAdmin(request);

        JsonNode json = JacksonUtility.readTree(requestBody);
        String id = json.path("id").asText();

        userManageService.removeUser(id);
        return Result.success();
    }
}
