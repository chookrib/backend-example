package com.example.backend.application.user;

import com.example.backend.utility.ValueUtility;

import java.time.LocalDateTime;

/**
 * 用户 DTO
 */
public class UserDto {

    private String id;
    private String username;
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

    public String getNickname() {
        return nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public boolean getIsAdmin() { return isAdmin; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UserDto(
            String id, String username, String nickname, String mobile, boolean isAdmin,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = ValueUtility.requireNotBlank(id);
        this.username = ValueUtility.requireNotBlank(username);
        this.nickname = ValueUtility.requireNotBlank(nickname);
        this.mobile = ValueUtility.requireNonNull(mobile);
        this.isAdmin = isAdmin;
        this.createdAt = ValueUtility.requireNonNull(createdAt);
        this.updatedAt = updatedAt;
    }
}
