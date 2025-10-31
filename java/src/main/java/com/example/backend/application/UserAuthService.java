package com.example.backend.application;

import com.example.backend.domain.User;
import com.example.backend.domain.UserRepository;
import com.example.backend.utility.CryptoUtility;
import com.example.backend.utility.ValueUtility;
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

    private int jwtExpiresDay;

    private String jwtSecretKey;

    private final UserRepository userRepository;

    public UserAuthService(
            @Value("${app.jwt-expires-day:}") String jwtExpiresDay,
            @Value("${app.jwt-secret-key:}") String jwtSecretKey,
            UserRepository userRepository) {
        Integer jwtExpiresDayValue = ValueUtility.toIntOrNull(jwtExpiresDay);
        if(jwtExpiresDayValue == null || jwtExpiresDayValue <= 0)
            throw new ApplicationException("app.jwt-expires-day 配置错误");
        this.jwtExpiresDay = jwtExpiresDayValue;
        if(ValueUtility.isBlank(jwtSecretKey))
            throw new ApplicationException("app.jwt-secret-key 配置错误");
        this.jwtSecretKey = jwtSecretKey;

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
                this.jwtSecretKey);
    }

    /**
     * 根据 AccessToken 获取登录用户 Id
     */
    public String getLoginUserId(String accessToken) {
        try {
            Map<String, String> payload = CryptoUtility.decodeJwt(accessToken, this.jwtSecretKey);
            return payload.getOrDefault("id", "");
        } catch (Exception ex) {
            return "";
        }
    }
}
