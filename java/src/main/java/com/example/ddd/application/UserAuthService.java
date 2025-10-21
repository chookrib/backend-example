package com.example.ddd.application;

import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.utility.CryptoUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证 Service
 */
@Component
public class UserAuthService {

    @Value("${app.user-jwt-expires-day}")
    private int jwtExpiresDay;

    @Value("${app.user-jwt-secret}")
    private String jwtSecret;

    private final UserRepository userRepository;

    public UserAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 登录，返回 AccessToken
     */
    public String login(String username, String password) {
        User user = this.userRepository.selectByUsername(username);
        if (user == null || !user.isPasswordMatch(password)) {
            throw new ApplicationException("密码错误");
        }
        Map<String, String> payload = new HashMap<>();
        payload.put("id", user.getId());
        return CryptoUtility.encodeJwt(
                payload,
                new Date(System.currentTimeMillis() + this.jwtExpiresDay * 24 * 60 * 60 * 1000L),
                this.jwtSecret);
    }

    /**
     * 根据 AccessToken 获取登录用户 Id
     */
    public String getLoginUserId(String accessToken) {
        try {
            Map<String, String> payload = CryptoUtility.decodeJwt(accessToken, this.jwtSecret);
            return payload.getOrDefault("id", "");
        } catch (Exception e) {
            return "";
        }
    }
}
