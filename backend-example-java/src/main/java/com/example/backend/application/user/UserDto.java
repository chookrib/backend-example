package com.example.backend.application.user;

import java.time.LocalDateTime;

/**
 * 用户 DTO
 */
public class UserDto {

    private String id;
    private String username;
    // private String password;
    private String nickname;
    private String mobile;
    private boolean isAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    // public String getPassword() { return password; }

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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
}
