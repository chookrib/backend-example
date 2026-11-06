package com.example.backend.adapter.driving.user;

import com.example.backend.adapter.driving.ControllerException;
import com.example.backend.adapter.driving.RequestIdentityResolver;
import com.example.backend.adapter.driving.RequestValueResolver;
import com.example.backend.adapter.driving.Result;
import com.example.backend.application.user.UserAuthService;
import com.example.backend.application.user.UserDto;
import com.example.backend.application.user.UserProfileService;
import com.example.backend.application.user.UserQueryHandler;
import com.example.backend.utility.JsonExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final RequestIdentityResolver requestIdentityResolver;
    private final UserAuthService userAuthService;
    private final UserProfileService userProfileService;
    private final UserQueryHandler userQueryHandler;

    public UserController(
            RequestIdentityResolver requestIdentityResolver,
            UserAuthService userAuthService,
            UserProfileService userProfileService,
            UserQueryHandler userQueryHandler
    ) {
        this.requestIdentityResolver = requestIdentityResolver;
        this.userAuthService = userAuthService;
        this.userProfileService = userProfileService;
        this.userQueryHandler = userQueryHandler;
    }

    /**
     * 注册
     */
    @RequestMapping(value = "/api/user/register", method = RequestMethod.POST)
    public Result register(@RequestBody String requestBody) {
        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String username = jsonExtractor.getNotBlankStringOrThrow("username", "用户名不能为空");
        String password = jsonExtractor.getNotBlankStringOrThrow("password", "密码不能为空");
        String confirmPassword = jsonExtractor.getNotBlankStringOrThrow("confirmPassword", "确认密码不能为空");
        String nickname = jsonExtractor.getNotBlankStringOrThrow("nickname", "昵称不能为空");

        if (!confirmPassword.equals(password))
            throw new ControllerException("两次输入的密码不一致");

        String userId = userProfileService.register(username, password, nickname);
        return Result.okData(Map.of(
                "id", userId
        ));
    }

    /**
     * 登录
     */
    @RequestMapping(value = "/api/user/login", method = RequestMethod.POST)
    public Result login(@RequestBody String requestBody) {
        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String username = jsonExtractor.getNotBlankStringOrThrow("username", "用户名不能为空");
        String password = jsonExtractor.getNotBlankStringOrThrow("password", "密码不能为空");

        String accessToken = userAuthService.login(username, password);
        return Result.okData(Map.of(
                "accessToken", accessToken
        ));
    }

    /**
     * 取用户资料
     */
    @RequestMapping(value = "/api/user/profile", method = RequestMethod.GET)
    public Result profile() {
        String userId = requestIdentityResolver.requireLoginUserId();
        UserDto userDto = userQueryHandler.queryByIdOrThrow(userId);
        return Result.okData(Map.of(
                "profile", userDto
        ));
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/api/user/modify-password", method = RequestMethod.POST)
    public Result modifyPassword(@RequestBody String requestBody) {
        String userId = requestIdentityResolver.requireLoginUserId();

        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String oldPassword = jsonExtractor.getNotBlankStringOrThrow("oldPassword", "旧密码不能为空");
        String newPassword = jsonExtractor.getNotBlankStringOrThrow("newPassword", "新密码不能为空");
        String confirmPassword = jsonExtractor.getNotBlankStringOrThrow("confirmPassword", "确认密码不能为空");

        if (!confirmPassword.equals(newPassword))
            throw new ControllerException("两次输入的密码不一致");

        userProfileService.modifyPassword(userId, oldPassword, newPassword);
        return Result.ok();
    }

    /**
     * 修改昵称
     */
    @RequestMapping(value = "/api/user/modify-nickname", method = RequestMethod.POST)
    public Result modifyNickname(@RequestBody String requestBody) {
        String userId = requestIdentityResolver.requireLoginUserId();

        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String nickname = jsonExtractor.getNotBlankStringOrThrow("nickname", "昵称不能为空");

        userProfileService.modifyNickname(userId, nickname);
        return Result.ok();
    }

    /**
     * 发送手机验证码
     */
    @RequestMapping(value = "/api/user/send-mobile-code", method = RequestMethod.POST)
    public Result sendMobileCode(@RequestBody String requestBody) {
        String userId = requestIdentityResolver.requireLoginUserId();
        userProfileService.sendMobileCode(userId);
        return Result.ok();
    }

    /**
     * 绑定手机
     */
    @RequestMapping(value = "/api/user/bind-mobile", method = RequestMethod.POST)
    public Result bindMobile(@RequestBody String requestBody) {
        String userId = requestIdentityResolver.requireLoginUserId();

        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String mobile = jsonExtractor.getNotBlankStringOrThrow("mobile", "手机号码不能为空");
        String code = jsonExtractor.getNotBlankStringOrThrow("code", "验证码不能为空");

        userProfileService.bindMobile(userId, mobile, code);
        return Result.ok();
    }
}
