package com.example.backend.application;

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
    private LocalDateTime updatedAt;

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    //public String getPassword() {
    //    return password;
    //}

    public String getNickname() {
        return this.nickname;
    }

    public String getMobile() {
        return this.mobile;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public UserDto(
            String id, String username, /*String password,*/ String nickname, String mobile, boolean isAdmin,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        //this.password = password;
        this.nickname = nickname;
        this.mobile = mobile;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    ///**
    // * 将用户对象转换为Map
    // */
    //public Map<String, ?> fromUser(User user) {
    //    return JsonUtility.convertValue(user);
    //}
}
