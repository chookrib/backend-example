package com.example.backend.application;

import com.example.backend.domain.User;
import com.example.backend.domain.UserRepository;
import com.example.backend.utility.CryptoUtility;
import com.example.backend.utility.ValueUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证 Service
 */
@Component
public class UserAuthService {

    private int jwtExpiresMinute;

    private String jwtSecret;

    private final UserRepository userRepository;

    public UserAuthService(
            @Value("${app.jwt-secret:}") String jwtSecret,
            @Value("${app.jwt-expires:}") String jwtExpires,
            UserRepository userRepository) {
        if (!ValueUtility.isEmptyString(jwtSecret)) {
            this.jwtSecret = jwtSecret;
        } else {
            throw new ApplicationException("app.jwt-secret 配置错误");
        }
        try {
            this.jwtExpiresMinute = CryptoUtility.jwtExpiresMinute(jwtExpires);
        } catch (Exception ex) {
            throw new ApplicationException("app.jwt-expires 配置错误");
        }
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
        return CryptoUtility.jwtEncode(
                payload,
                this.jwtSecret,
                LocalDateTime.now().plusMinutes(this.jwtExpiresMinute)
        );
    }

    /**
     * 根据 AccessToken 获取登录用户 Id
     */
    public String getLoginUserId(String accessToken) {
        try {
            Map<String, ?> payload = CryptoUtility.jwtDecode(accessToken, this.jwtSecret);
            return String.valueOf(payload.get("id"));
        } catch (Exception ex) {
            return "";
        }
    }
}
