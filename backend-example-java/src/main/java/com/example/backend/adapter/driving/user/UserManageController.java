package com.example.backend.adapter.driving.user;

import com.example.backend.adapter.driving.RequestIdentityResolver;
import com.example.backend.adapter.driving.RequestValueResolver;
import com.example.backend.adapter.driving.Result;
import com.example.backend.application.user.UserDto;
import com.example.backend.application.user.UserQueryCriteria;
import com.example.backend.application.user.UserQueryHandler;
import com.example.backend.application.user.UserManageService;
import com.example.backend.utility.JsonExtractor;
import com.example.backend.utility.ValueUtility;
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
        requestIdentityResolver.checkAdminPermission();

        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        int pageNum = RequestValueResolver.normalizePageNum(jsonExtractor.getString("pageNum"));
        int pageSize = RequestValueResolver.normalizePageSize(jsonExtractor.getString("pageSize"));

        UserQueryCriteria criteria = new UserQueryCriteria();
        criteria.setKeyword(jsonExtractor.getStringOrEmpty("criteria.keyword"));

        LinkedHashMap<String, String> sortOptions = RequestValueResolver.getSortOptions(
                jsonExtractor.getNode("sort")
        );

        int totalCount = userQueryHandler.queryCount(criteria);
        RequestValueResolver.Paging paging = RequestValueResolver.normalizePaging(pageNum, pageSize, totalCount);
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
        requestIdentityResolver.checkAdminPermission();

        String id = ValueUtility.requireNotBlank(request.getParameter("id"), "缺少 Id 参数");
        UserDto userDto = userQueryHandler.queryByIdOrThrow(id);
        return Result.okData(Map.of(
                "item", userDto
        ));
    }

    /**
     * 创建用户
     */
    @RequestMapping(value = "/api/admin/user/create", method = RequestMethod.POST)
    public Result userCreate(@RequestBody String requestBody) {
        requestIdentityResolver.checkAdminPermission();

        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String username = jsonExtractor.getNotBlankStringOrThrow("username", "用户名不能为空");
        String password = jsonExtractor.getNotBlankStringOrThrow("password", "密码不能为空");
        String nickname = jsonExtractor.getNotBlankStringOrThrow("nickname", "昵称不能为空");
        String mobile = jsonExtractor.getStringOrEmpty("mobile");
        boolean isAdmin = ValueUtility.toBoolOrThrow(jsonExtractor.getString("isAdmin"), "是否管理员值无效");

        String userId = userManageService.createUser(username, password, nickname, mobile, isAdmin);
        return Result.okData(Map.of(
                "id", userId
        ));
    }

    /**
     * 修改用户
     */
    @RequestMapping(value = "/api/admin/user/update", method = RequestMethod.POST)
    public Result userUpdate(@RequestBody String requestBody) {
        requestIdentityResolver.checkAdminPermission();

        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String id = jsonExtractor.getNotBlankStringOrThrow("id", "Id 不能为空");
        String username = jsonExtractor.getNotBlankStringOrThrow("username", "用户名不能为空");
        String nickname = jsonExtractor.getNotBlankStringOrThrow("nickname", "昵称不能为空");
        String mobile = jsonExtractor.getStringOrEmpty("mobile");
        boolean isAdmin = ValueUtility.toBoolOrThrow(jsonExtractor.getString("isAdmin"), "是否管理员值无效");

        userManageService.updateUser(id, username, nickname, mobile, isAdmin);
        return Result.ok();
    }

    /**
     * 删除用户
     */
    @RequestMapping(value = "/api/admin/user/delete", method = RequestMethod.POST)
    public Result userDelete(@RequestBody String requestBody) {
        requestIdentityResolver.checkAdminPermission();

        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String id = jsonExtractor.getNotBlankStringOrThrow("id", "Id 不能为空");

        userManageService.deleteUser(id);
        return Result.ok();
    }
}
