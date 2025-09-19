package com.example.ddd.application;

import com.example.ddd.domain.User;
import com.example.ddd.utility.JacksonUtility;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户DTO
 */
public class UserDto {

    private String id;
    private String username;
    //private String password;
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

    //public String getPassword() {
    //    return password;
    //}

    public String getNickname() {
        return nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UserDto(
            String id, String username, /*String password,*/ String nickname, String mobile, String email,
            boolean isAdmin, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        //this.password = password;
        this.nickname = nickname;
        this.mobile = mobile;
        this.email = email;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
    }

    /**
     * 将用户对象转换为Map
     */
    public Map<String, ?> fromUser(User user) {
        return JacksonUtility.convertValue(user);
    }
}
