package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserApplicationService;
import com.example.ddd.domain.User;
import com.example.ddd.domain.ValidationException;
import com.example.ddd.utility.JacksonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller
 */
@RestController
public class UserController {
    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    /**
     * 注册
     */
    @RequestMapping(value = "/api/user/register", method = RequestMethod.POST)
    public Result register(@RequestBody String requestBody) {
        JsonNode json = JacksonUtility.readTree(requestBody);
        String username = json.path("username").asText();
        String password = json.path("password").asText();
        String confirmPassword = json.path("confirmPassword").asText();
        String nickname = json.path("nickname").asText();
        String mobile = json.path("mobile").asText();
        String email = json.path("email").asText();

        if(!confirmPassword.equals(password)) {
            throw new ValidationException("两次输入的密码不一致");
        }

        String id = userApplicationService.register(username, password, nickname, mobile, email);
        return Result.successData(id);
    }

    /**
     * 登录
     */
    @RequestMapping(value = "/api/user/login", method = RequestMethod.POST)
    public Result login(@RequestBody String requestBody) {
        JsonNode json = JacksonUtility.readTree(requestBody);
        String username = json.path("username").asText();
        String password = json.path("password").asText();

        String accessToken = userApplicationService.login(username, password);
        return Result.successData(accessToken);
    }

    /**
     * 取用户
     */
    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)
    public Result get(@PathVariable String id) {
        User entity = userRepository.selectById(id);
        return Result.successData(entity);
    }

}
