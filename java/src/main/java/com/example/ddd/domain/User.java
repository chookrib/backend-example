package com.example.ddd.domain;

import com.auth0.jwt.interfaces.Claim;
import com.example.ddd.utility.IdUtility;
import com.example.ddd.utility.JwtUtility;
import com.example.ddd.utility.Md5Utility;
import io.micrometer.common.util.StringUtils;

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
    private boolean isAdmin;
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

    public boolean isAdmin() { return isAdmin; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private User() {
    }

    /**
     * 还原用户
     */
    public static User restoreUser(
            String id, String username, String password, String nickname, String mobile, String email, boolean isAdmin,
            LocalDateTime createdAt) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = password;
        user.nickname = nickname;
        user.mobile = mobile;
        user.email = email;
        user.isAdmin = isAdmin;
        user.createdAt = createdAt;
        return user;
    }

    /**
     * 创建用户
     */
    public static User createUser(
            String username, String password, String nickname, String mobile, String email,
            UserUniqueChecker userUniqueChecker) {
        if (userUniqueChecker != null) {
            if (!userUniqueChecker.isUsernameUnique(username)) {
                throw new DomainException("用户名已存在");
            }
        }
        User user = new User();
        user.id = IdUtility.generateId();
        user.username = username;
        user.password = Md5Utility.generateMd5(password);
        user.nickname = nickname;
        user.mobile = mobile;
        user.email = email;
        user.isAdmin = false;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    /**
     * 检查密码是否匹配
     */
    public boolean isPasswordMatch(String password) {
        return this.password.equals(Md5Utility.generateMd5(password));
    }

    /**
     * 设置是否管理员
     */
    public void setAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    /**
     * 生成访问令牌
     */
    public String encodeAccessToken(int expiresDay, String secret) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        return JwtUtility.encode(map, new Date(System.currentTimeMillis() + expiresDay * 24 * 60 * 60 * 1000L), secret);
    }

    /**
     * 解码访问令牌
     */
    public static String decodeAccessToken(String accessToken, String jwtSecret) {
        Map<String, Claim> token = JwtUtility.decode(accessToken, jwtSecret);
        return token.get("id").asString();
    }
}
