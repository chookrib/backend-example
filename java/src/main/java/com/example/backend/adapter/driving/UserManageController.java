package com.example.backend.adapter.driving;

import com.example.backend.application.UserDto;
import com.example.backend.application.UserManageService;
import com.example.backend.application.UserQueryCriteria;
import com.example.backend.application.UserQueryHandler;
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
        RequestAuthHelper.requireLoginUserAdmin(request);

        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        int pageNum = RequestValueHelper.getRequestJsonInt(requestJson, 1, "pageNum");
        int pageSize = RequestValueHelper.getRequestJsonInt(requestJson, 1, "pageSize");

        //JsonNode criteriaJson = requestJson.path("criteria");
        UserQueryCriteria criteria = new UserQueryCriteria();
        //if (!criteriaJson.isMissingNode()) {
            //String keyword = criteriaJson.path("keyword").asText().trim();
            String keyword = RequestValueHelper.getRequestJsonStringTrimOrEmpty(requestJson,"criteria", "keyword");
            criteria.setKeyword(keyword);
        //}

        int totalCount = this.userQueryHandler.queryCount(criteria);
        RequestValueHelper.Paging paging = RequestValueHelper.fixPaging(pageNum, pageSize, totalCount);
        List<UserDto> list = this.userQueryHandler.queryByPage(paging.pageNum(), paging.pageSize(), criteria);
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
        RequestAuthHelper.requireLoginUserAdmin(request);

        String id = RequestValueHelper.getRequestParamStringTrimReq(request, "id");
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
        RequestAuthHelper.requireLoginUserAdmin(request);

        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        String username = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "username");
        String password = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "password");
        String nickname = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "nickname");
        String mobile = RequestValueHelper.getRequestJsonStringTrimOrEmpty(requestJson, "mobile");

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
        RequestAuthHelper.requireLoginUserAdmin(request);

        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        String id = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "id");
        String username = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "username");
        String nickname = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "nickname");
        String mobile = RequestValueHelper.getRequestJsonStringTrimOrEmpty(requestJson, "mobile");

        this.userManageService.modifyUser(id, username, nickname, mobile);
        return Result.ok();
    }


    /**
     * 删除用户
     */
    @RequestMapping(value = "/api/admin/user/remove", method = RequestMethod.POST)
    public Result userRemove(HttpServletRequest request, @RequestBody String requestBody) {
        RequestAuthHelper.requireLoginUserAdmin(request);

        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        String id = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "id");

        this.userManageService.removeUser(id);
        return Result.ok();
    }
}
