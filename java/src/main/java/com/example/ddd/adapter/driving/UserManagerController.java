package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserDto;
import com.example.ddd.application.UserQueryCriteria;
import com.example.ddd.application.UserQueryHandler;
import com.example.ddd.utility.JacksonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理Controller
 */
@RestController
public class UserManagerController {

    private final UserQueryHandler userQueryHandler;

    public UserManagerController(UserQueryHandler userQueryHandler) {
        this.userQueryHandler = userQueryHandler;
    }

    /**
     * 用户列表
     */
    @RequestMapping(value = "/api/admin/user/list", method = RequestMethod.POST)
    public Result userList(@RequestBody String requestBody) {

        JsonNode json = JacksonUtility.readTree(requestBody);
        int pageIndex = json.path("pageIndex").asInt(1);
        int pageSize = json.path("pageSize").asInt(0);

        JsonNode criteriaJson = json.path("criteria");
        UserQueryCriteria criteria = new UserQueryCriteria();
        if (!criteriaJson.isMissingNode()) {
            String keyword = criteriaJson.path("keyword").asText();
            criteria.setKeyword(keyword);
        }

        int count = userQueryHandler.queryCount(criteria);
        List<UserDto> list = userQueryHandler.queryByPage(pageIndex, pageSize, criteria);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("page", Map.of("totalCount", count, "pageIndex", pageIndex, "pageSize", pageSize));
        return Result.successData(map);
    }

    /**
     * 用户列表
     */
    @RequestMapping(value = "/api/admin/user/get/", method = RequestMethod.GET)
    public Result userGet(@RequestParam String id) {

        UserDto dto = userQueryHandler.queryById(id);
        return Result.successData(dto);
    }
}
