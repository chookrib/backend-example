package com.example.ddd.domain;

import com.example.ddd.utility.CryptoUtility;
import com.example.ddd.utility.ValueUtility;

import java.time.LocalDateTime;

/**
 * 用户 Entity
 */
public class User {

    private String id;
    private String username;
    private String password;
    private String nickname;
    private String mobile;
    private boolean isAdmin;
    private LocalDateTime createdAt;

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getMobile() {
        return this.mobile;
    }

    public boolean isAdmin() { return this.isAdmin; }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    private User() {
    }

    /**
     * 还原用户
     */
    public static User restoreUser(
            String id, String username, String password, String nickname, String mobile, boolean isAdmin,
            LocalDateTime createdAt) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = password;
        user.nickname = nickname;
        user.mobile = mobile;
        user.isAdmin = isAdmin;
        user.createdAt = createdAt;
        return user;
    }

    /**
     * 注册用户
     */
    public static User registerUser(
            String id, String username, String password, String nickname, UserUniqueSpecification userUniqueSpecification) {
        if (ValueUtility.isBlank(username))
            throw new DomainException("用户名不能为空");

        if (ValueUtility.isBlank(password))
            throw new DomainException("密码不能为空");

        if (ValueUtility.isBlank(nickname))
            throw new DomainException("昵称不能为空");

        if (userUniqueSpecification != null) {
            if (!userUniqueSpecification.isUsernameUnique(username))
                throw new DomainException("用户名已存在");

            if (!userUniqueSpecification.isNicknameUnique(nickname))
                throw new DomainException("昵称已存在");
        }

        User user = new User();
        user.id = id;
        user.username = username;
        user.password = CryptoUtility.encodeMd5(password);
        user.nickname = nickname;
        user.mobile = "";
        user.isAdmin = false;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    /**
     * 检查密码是否匹配
     */
    public boolean isPasswordMatch(String password) {
        return this.password.equals(CryptoUtility.encodeMd5(password));
    }

    /**
     * 设置是否管理员
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * 修改密码
     */
    public void modifyPassword(String oldPassword, String newPassword) {
        if (ValueUtility.isBlank(newPassword))
            throw new DomainException("密码不能为空");

        if (!isPasswordMatch(oldPassword)) {
            throw new DomainException("密码错误");
        }

        this.password = CryptoUtility.encodeMd5(newPassword);
    }

    /**
     * 修改昵称
     */
    public void modifyNickname(String nickname, UserUniqueSpecification userUniqueSpecification) {
        if (ValueUtility.isBlank(nickname))
            throw new DomainException("昵称不能为空");

        if (!nickname.equalsIgnoreCase(this.nickname) && userUniqueSpecification != null) {
            if (!userUniqueSpecification.isNicknameUnique(nickname))
                throw new DomainException("昵称已存在");
        }

        this.nickname = nickname;
    }

    /**
     * 修改手机
     */
    public void modifyMobile(String mobile, UserUniqueSpecification userUniqueSpecification) {
        if (ValueUtility.isBlank(mobile))
            throw new DomainException("手机不能为空");

        if (!mobile.equalsIgnoreCase(this.mobile) && userUniqueSpecification != null) {
            if (!userUniqueSpecification.isMobileUnique(mobile))
                throw new DomainException("手机已存在");
        }

        this.mobile = mobile;
    }

    /**
     * 创建用户
     */
    public static User createUser(
            String id, String username, String password, String nickname, String mobile, UserUniqueSpecification userUniqueSpecification) {
        if (ValueUtility.isBlank(username))
            throw new DomainException("用户名不能为空");

        if (ValueUtility.isBlank(password))
            throw new DomainException("密码不能为空");

        if (ValueUtility.isBlank(nickname))
            throw new DomainException("昵称不能为空");

        if (userUniqueSpecification != null) {
            if (!userUniqueSpecification.isUsernameUnique(username))
                throw new DomainException("用户名已存在");

            if (!userUniqueSpecification.isNicknameUnique(nickname))
                throw new DomainException("昵称已存在");

            if (!ValueUtility.isBlank(mobile) && !userUniqueSpecification.isMobileUnique(mobile))
                throw new DomainException("手机已存在");
        }

        User user = new User();
        user.id = id;
        user.username = username;
        user.password = CryptoUtility.encodeMd5(password);
        user.nickname = nickname;
        user.mobile = mobile;
        user.isAdmin = false;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    /**
     * 修改用户
     */
    public void modify(String username, String nickname, String mobile, UserUniqueSpecification userUniqueSpecification) {
        if (ValueUtility.isBlank(username))
            throw new DomainException("用户名不能为空");

        if (ValueUtility.isBlank(nickname))
            throw new DomainException("昵称不能为空");

        if (userUniqueSpecification != null) {
            if (!username.equalsIgnoreCase(this.username) && !userUniqueSpecification.isUsernameUnique(username))
                throw new DomainException("用户名已存在");

            if (!nickname.equalsIgnoreCase(this.nickname) && !userUniqueSpecification.isNicknameUnique(nickname))
                throw new DomainException("昵称已存在");

            if (!ValueUtility.isBlank(mobile) && !mobile.equalsIgnoreCase(this.mobile) && !userUniqueSpecification.isMobileUnique(mobile))
                throw new DomainException("手机已存在");
        }

        this.username = username;
        this.nickname = nickname;
        this.mobile = mobile;
    }
}
