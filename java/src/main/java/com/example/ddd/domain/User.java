package com.example.ddd.domain;

import com.example.ddd.application.ApplicationException;
import com.example.ddd.utility.IdUtility;
import com.example.ddd.utility.Md5Utility;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户
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
            String username, String password, String nickname, UserUniqueChecker userUniqueChecker) {
        if(StringUtils.isBlank(username))
            throw new DomainException("用户名不能为空");

        if(StringUtils.isBlank(password))
            throw new DomainException("密码不能为空");

        if(StringUtils.isBlank(nickname))
            throw new DomainException("昵称不能为空");

        if (userUniqueChecker != null) {
            if (!userUniqueChecker.isUsernameUnique(username))
                throw new DomainException("用户名已存在");

            if(!userUniqueChecker.isNicknameUnique(nickname))
                throw new DomainException("昵称已存在");
        }

        User user = new User();
        user.id = IdUtility.generateId();
        user.username = username;
        user.password = Md5Utility.generateMd5(password);
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
        return this.password.equals(Md5Utility.generateMd5(password));
    }

    /**
     * 设置是否管理员
     */
    public void setAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    /**
     * 修改密码
     */
    public void modifyPassword(String oldPassword, String newPassword) {
        if(StringUtils.isBlank(newPassword))
            throw new RuntimeException("密码不能为空");

        if (!isPasswordMatch(oldPassword)) {
            throw new ApplicationException("密码错误");
        }

        this.password = Md5Utility.generateMd5(password);
    }

    /**
     * 修改昵称
     */
    public void modifyNickname(String nickname, UserUniqueChecker userUniqueChecker) {
        if(StringUtils.isBlank(nickname))
            throw new DomainException("昵称不能为空");

        if(!nickname.equalsIgnoreCase(this.nickname) && userUniqueChecker != null) {
            if(!userUniqueChecker.isNicknameUnique(nickname))
                throw new DomainException("昵称已存在");
        }

        this.nickname = nickname;
    }

    /**
     * 修改手机
     */
    public void modifyMobile(String mobile, UserUniqueChecker userUniqueChecker) {
        if(StringUtils.isBlank(mobile))
            throw new DomainException("手机不能为空");

        if(!mobile.equalsIgnoreCase(this.mobile) && userUniqueChecker != null) {
            if(!userUniqueChecker.isMobileUnique(mobile))
                throw new DomainException("手机已存在");
        }

        this.mobile = mobile;
    }

    /**
     * 创建用户
     */
    public static User createUser(
            String username, String password, String nickname, String mobile, UserUniqueChecker userUniqueChecker) {
        if(StringUtils.isBlank(username))
            throw new DomainException("用户名不能为空");

        if(StringUtils.isBlank(password))
            throw new DomainException("密码不能为空");

        if(StringUtils.isBlank(nickname))
            throw new DomainException("昵称不能为空");

        if (userUniqueChecker != null) {
            if (!userUniqueChecker.isUsernameUnique(username))
                throw new DomainException("用户名已存在");

            if(!userUniqueChecker.isNicknameUnique(nickname))
                throw new DomainException("昵称已存在");

            if(!StringUtils.isBlank(mobile) && !userUniqueChecker.isMobileUnique(mobile))
                throw new DomainException("手机已存在");
        }

        User user = new User();
        user.id = IdUtility.generateId();
        user.username = username;
        user.password = Md5Utility.generateMd5(password);
        user.nickname = nickname;
        user.mobile = mobile;
        user.isAdmin = false;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    /**
     * 修改用户信息
     */
    public void modify(String username, String nickname, String mobile, UserUniqueChecker userUniqueChecker) {
        if(StringUtils.isBlank(username))
            throw new DomainException("用户名不能为空");

        if(StringUtils.isBlank(nickname))
            throw new DomainException("昵称不能为空");

        if (userUniqueChecker != null) {
            if (!username.equalsIgnoreCase(this.username) && !userUniqueChecker.isUsernameUnique(username))
                throw new DomainException("用户名已存在");

            if(!nickname.equalsIgnoreCase(this.nickname) && !userUniqueChecker.isNicknameUnique(nickname))
                throw new DomainException("昵称已存在");

            if(!mobile.equalsIgnoreCase(this.mobile) && !userUniqueChecker.isMobileUnique(mobile))
                throw new DomainException("手机已存在");
        }

        this.username = username;
        this.nickname = nickname;
        this.mobile = mobile;
    }
}
