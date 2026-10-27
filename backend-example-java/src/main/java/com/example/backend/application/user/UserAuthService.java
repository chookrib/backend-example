package com.example.backend.application.user;

import com.example.backend.application.ApplicationException;
import com.example.backend.domain.user.User;
import com.example.backend.domain.user.UserRepository;
import com.example.backend.utility.CryptoUtility;
import com.example.backend.utility.ValueUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用户认证 Service
 */
@Component
public class UserAuthService {

    private final UserRepository userRepository;
    private final String jwtSecret;
    private final int jwtTtlMinute;

    public UserAuthService(
            UserRepository userRepository,
            @Value("${app.jwt-secret}") String jwtSecret,
            @Value("${app.jwt-ttl}") String jwtTtl) {
        this.userRepository = userRepository;

        if (!ValueUtility.isEmptyString(jwtSecret)) {
            this.jwtSecret = jwtSecret;
        } else {
            throw new ApplicationException("app.jwt-secret 配置错误");
        }

        try {
            this.jwtTtlMinute = CryptoUtility.jwtTtlToMinute(jwtTtl);
        } catch (Exception ex) {
            throw new ApplicationException("app.jwt-ttl 配置错误");
        }

    }

    /**
     * 登录，返回 AccessToken
     */
    public String login(String username, String password) {
        User user = userRepository.selectByUsername(username);
        if (user == null || !user.isPasswordMatch(password)) {
            throw new ApplicationException("密码错误");
        }
        Map<String, String> payload = new HashMap<>();
        payload.put("id", user.getId());
        return CryptoUtility.jwtEncode(
                payload,
                jwtSecret,
                LocalDateTime.now().plusMinutes(jwtTtlMinute)
        );
    }

    /**
     * 根据 AccessToken 获取登录用户 Id
     */
    public String getLoginUserId(String accessToken) {
        if (ValueUtility.isEmptyString(accessToken)) {
            return "";
        }

        try {
            Map<String, ?> payload = CryptoUtility.jwtDecode(accessToken, jwtSecret);
            // return String.valueOf(payload.get("id"));    // 不存在时返回字符串 "null"
            return Objects.toString(payload.get("id"), "");
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 根据 AccessToken 获取登录用户
     */
    public User getLoginUser(String accessToken) {
        String loginUserId = getLoginUserId(accessToken);
        if (ValueUtility.isEmptyString(loginUserId)) {
            return null;
        }

        return userRepository.selectById(loginUserId);
    }
}
