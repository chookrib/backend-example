package com.example.ddd.application;

import java.time.LocalDateTime;

/**
 * 用户 DTO
 */
public class UserDto {

    private String id;
    private String username;
    //private String password;
    private String nickname;
    private String mobile;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UserDto(
            String id, String username, /*String password,*/ String nickname, String mobile, boolean isAdmin,
            LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        //this.password = password;
        this.nickname = nickname;
        this.mobile = mobile;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
    }

    ///**
    // * 将用户对象转换为Map
    // */
    //public Map<String, ?> fromUser(User user) {
    //    return JsonUtility.convertValue(user);
    //}
}
