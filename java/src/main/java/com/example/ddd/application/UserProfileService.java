package com.example.ddd.application;

import com.example.ddd.domain.EmailGateway;
import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.UserUniqueChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 用户资料服务
 */
@Component
public class UserProfileService {

    @Value("${application.user-jwt-expires-day}")
    private int jwtExpiresDay;

    @Value("${application.user-jwt-secret}")
    private String jwtSecret;

    private final UserRepository userRepository;
    private final UserUniqueChecker userUniqueChecker;
    private final EmailGateway emailGateway;

    public UserProfileService(
            UserRepository userRepository, UserUniqueChecker userUniqueChecker, EmailGateway emailGateway) {
        this.userRepository = userRepository;
        this.userUniqueChecker = userUniqueChecker;
        this.emailGateway = emailGateway;
    }

    /**
     * 注册
     */
    public String register(String username, String password, String nickname, String mobile, String email) {
        User user = User.createUser(username, password, nickname, mobile, email, userUniqueChecker);
        userRepository.insert(user);
        emailGateway.sendEmail(email, "注册成功", "欢迎您，" + nickname + "，注册成功！");
        return user.getId();
    }

    /**
     * 登录，返回AccessToken
     */
    public String login(String username, String password) {
        User user = userRepository.selectByUsername(username);
        if(user == null || !user.isPasswordMatch(password)) {
            throw new ApplicationException("密码错误");
        }
        return user.encodeAccessToken(jwtExpiresDay, jwtSecret);
    }

    /**
     * 根据AccessToken取登录用户Id
     */
    public String decodeAccessToken(String accessToken) {
        try {
            return User.decodeAccessToken(accessToken, jwtSecret);
        }
        catch (Exception e) {
            return "";
        }
    }
}
