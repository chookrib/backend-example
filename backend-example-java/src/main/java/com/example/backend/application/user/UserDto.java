package com.example.backend.application.user;

import java.time.LocalDateTime;
import java.util.Objects;

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
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.nickname = Objects.requireNonNull(nickname);
        this.mobile = Objects.requireNonNull(mobile);
        this.isAdmin = isAdmin;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;
    }
}
