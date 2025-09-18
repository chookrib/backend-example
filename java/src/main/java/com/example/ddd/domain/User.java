package com.example.ddd.domain;

import com.example.ddd.utility.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户
 */
public class User {

    private String id;
    private String username;
    private String password;
    private String nickname;
    private String mobile;
    private String email;
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private User() {
    }

    /**
     * 还原用户
     */
    public static User restoreUser(
            String id, String username, String password, String nickname, String mobile, String email,
            LocalDateTime createdAt) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = password;
        user.nickname = nickname;
        user.mobile = mobile;
        user.email = email;
        user.createdAt = createdAt;
        return user;
    }

    /**
     * 创建用户
     */
    public static User createUser(String username, String password, String nickname, String mobile, String email) {
        User user = new User();
        user.id = IdUtility.generateId();
        user.username = username;
        user.password = Md5Utility.md5(password);
        user.nickname = nickname;
        user.mobile = mobile;
        user.email = email;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    /**
     * 登录，成功返回令牌
     */
    public String login(String password) {
        if (!this.password.equals(Md5Utility.md5(password))) {
            throw new DomainException("密码错误");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);

        return JwtUtility.encode(map, new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L), "");
    }
}
