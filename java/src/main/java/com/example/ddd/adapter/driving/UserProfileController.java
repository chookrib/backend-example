package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserDto;
import com.example.ddd.application.UserProfileService;
import com.example.ddd.application.UserQueryHandler;
import com.example.ddd.utility.JacksonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 用户资料Controller
 */
@RestController
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final RequestHandler requestHandler;
    private final UserQueryHandler userQueryHandler;

    public UserProfileController(
            UserProfileService userProfileService, RequestHandler requestHandler, UserQueryHandler userQueryHandler) {
        this.userProfileService = userProfileService;
        this.requestHandler = requestHandler;
        this.userQueryHandler = userQueryHandler;
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
            throw new ControllerException("两次输入的密码不一致");
        }

        String id = userProfileService.register(username, password, nickname, mobile, email);
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

        String accessToken = userProfileService.login(username, password);
        return Result.successData(accessToken);
    }

    /**
     * 取用户资料
     */
    @RequestMapping(value = "/api/user/profile", method = RequestMethod.GET)
    public Result profile(HttpServletRequest request) {
        String id = requestHandler.requireLoginUserId(request);
        UserDto dto = userQueryHandler.queryById(id);
        return Result.successData(dto);
    }
}
