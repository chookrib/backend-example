package com.example.ddd.adapter.driving;

import com.example.ddd.application.UserAuthService;
import com.example.ddd.application.UserDto;
import com.example.ddd.application.UserProfileService;
import com.example.ddd.application.UserQueryHandler;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户 Controller
 */
@RestController
public class UserController {

    private final UserAuthService userAuthService;
    private final UserProfileService userProfileService;
    private final UserQueryHandler userQueryHandler;

    public UserController(
            UserAuthService userAuthService,
            UserProfileService userProfileService,
            UserQueryHandler userQueryHandler
    ) {
        this.userAuthService = userAuthService;
        this.userProfileService = userProfileService;
        this.userQueryHandler = userQueryHandler;
    }

    /**
     * 注册
     */
    @RequestMapping(value = " ", method = RequestMethod.POST)
    public Result register(@RequestBody String requestBody) {
        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String username = requestJson.path("username").asText().trim();
        String password = requestJson.path("password").asText().trim();
        String confirmPassword = requestJson.path("confirmPassword").asText().trim();
        String nickname = requestJson.path("nickname").asText().trim();

        if (!confirmPassword.equals(password)) {
            throw new ControllerException("两次输入的密码不一致");
        }

        String userId = this.userProfileService.register(username, password, nickname);
        return Result.okData(Map.of(
                "id", userId
        ));
    }

    /**
     * 登录
     */
    @RequestMapping(value = "/api/user/login", method = RequestMethod.POST)
    public Result login(@RequestBody String requestBody) {
        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String username = requestJson.path("username").asText().trim();
        String password = requestJson.path("password").asText().trim();

        String accessToken = this.userAuthService.login(username, password);
        return Result.okData(Map.of(
                "accessToken", accessToken
        ));
    }

    /**
     * 取用户资料
     */
    @RequestMapping(value = "/api/user/profile", method = RequestMethod.GET)
    public Result profile(HttpServletRequest request) {
        String userId = RequestHelper.requireLoginUserId(request);
        UserDto userDto = this.userQueryHandler.queryById(userId);
        return Result.okData(Map.of(
                "profile", userDto
        ));
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/api/user/modify-password", method = RequestMethod.POST)
    public Result modifyPassword(HttpServletRequest request, @RequestBody String requestBody) {
        String userId = RequestHelper.requireLoginUserId(request);

        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String oldPassword = requestJson.path("oldPassword").asText().trim();
        String newPassword = requestJson.path("newPassword").asText().trim();
        String confirmPassword = requestJson.path("confirmPassword").asText().trim();

        if (!confirmPassword.equals(newPassword)) {
            throw new ControllerException("两次输入的密码不一致");
        }

        this.userProfileService.modifyPassword(userId, oldPassword, newPassword);
        return Result.ok();
    }

    /**
     * 修改昵称
     */
    @RequestMapping(value = "/api/user/modify-nickname", method = RequestMethod.POST)
    public Result modifyNickname(HttpServletRequest request, @RequestBody String requestBody) {
        String userId = RequestHelper.requireLoginUserId(request);

        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String nickname = requestJson.path("nickname").asText().trim();
        this.userProfileService.modifyNickname(userId, nickname);
        return Result.ok();
    }

    /**
     * 发送手机验证码
     */
    @RequestMapping(value = "/api/user/send-mobile-code", method = RequestMethod.POST)
    public Result sendMobileCode(HttpServletRequest request, @RequestBody String requestBody) {
        String userId = RequestHelper.requireLoginUserId(request);

        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String mobile = requestJson.path("mobile").asText().trim();

        this.userProfileService.sendMobileCode(userId, mobile);
        return Result.ok();
    }

    /**
     * 绑定手机
     */
    @RequestMapping(value = "/api/user/bind-mobile", method = RequestMethod.POST)
    public Result bindMobile(HttpServletRequest request, @RequestBody String requestBody) {
        String userId = RequestHelper.requireLoginUserId(request);

        JsonNode requestJson = RequestHelper.toJson(requestBody);
        String mobile = requestJson.path("mobile").asText().trim();
        String code = requestJson.path("code").asText().trim();
        this.userProfileService.bindMobile(userId, mobile, code);
        return Result.ok();
    }
}
