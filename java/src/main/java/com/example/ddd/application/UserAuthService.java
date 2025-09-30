package com.example.ddd.application;

import com.auth0.jwt.interfaces.Claim;
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

    @Value("${application.user-jwt-expires-day}")
    private int jwtExpiresDay;

    @Value("${application.user-jwt-secret}")
    private String jwtSecret;

    private final UserRepository userRepository;

    public UserAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 登录，返回 AccessToken
     */
    public String login(String username, String password) {
        User user = userRepository.selectByUsername(username);
        if (user == null || !user.isPasswordMatch(password)) {
            throw new ApplicationException("密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        return CryptoUtility.encodeJwt(
                map,
                new Date(System.currentTimeMillis() + jwtExpiresDay * 24 * 60 * 60 * 1000L),
                jwtSecret);
    }

    /**
     * 根据 AccessToken 获取登录用户 Id
     */
    public String getLoginUserId(String accessToken) {
        try {
            Map<String, Claim> token = CryptoUtility.decodeJwt(accessToken, jwtSecret);
            return token.get("id").asString();
        } catch (Exception e) {
            return "";
        }
    }
}
