package com.example.backend.domain.user;

import com.example.backend.domain.DomainException;
import com.example.backend.utility.CryptoUtility;
import com.example.backend.utility.ValueUtility;

import java.time.LocalDateTime;

/**
 * 用户 Entity
 */
public class User {

    private String id;
    private String username;
    private String password;                    // 加密后的密码
    private String nickname;
    private String mobile;
    private boolean isAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() { return id; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getNickname() { return nickname; }

    public String getMobile() { return mobile; }

    public boolean getIsAdmin() { return isAdmin; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    private User() {
    }

    /**
     * 重建
     */
    public static User reconstitute(
            String id, String username, String password, String nickname, String mobile, boolean isAdmin,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        User user = new User();
        user.id = ValueUtility.requireNotBlank(id);
        user.username = ValueUtility.requireNotBlank(username);
        user.password = ValueUtility.requireNotBlank(password);
        user.nickname = ValueUtility.requireNotBlank(nickname);
        user.mobile = ValueUtility.requireNonNull(mobile);
        user.isAdmin = isAdmin;
        user.createdAt = ValueUtility.requireNonNull(createdAt);
        user.updatedAt = updatedAt;
        return user;
    }

    /**
     * 注册
     */
    public static User register(
            String id, String username, String password, String nickname, UserUniquenessValidator userUniquenessValidator) {
        id = ValueUtility.requireNotBlank(id, "用户 Id 不能为空");
        username = ValueUtility.requireNotBlank(username, "用户名不能为空");
        password = ValueUtility.requireNotBlank(password, "密码不能为空");
        nickname = ValueUtility.requireNotBlank(nickname, "昵称不能为空");

        if (userUniquenessValidator != null) {
            if (!userUniquenessValidator.isUsernameAvailable(username))
                throw new DomainException("用户名已存在");

            if (!userUniquenessValidator.isNicknameAvailable(nickname))
                throw new DomainException("昵称已存在");
        }

        User user = new User();
        user.id = id;
        user.username = username;
        user.password = CryptoUtility.md5Encode(password);
        user.nickname = nickname;
        user.mobile = "";
        user.isAdmin = false;
        user.createdAt = ValueUtility.getCurrentDateTime();
        user.updatedAt = null;
        return user;
    }

    /**
     * 检查密码是否匹配
     */
    public boolean isPasswordMatch(String password) {
        if (ValueUtility.isBlank(password))
            return false;   // 空密码永远返回否
        return this.password.equals(CryptoUtility.md5Encode(password));
    }

    /**
     * 设置是否管理员
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.updatedAt = ValueUtility.getCurrentDateTime();
    }

    /**
     * 修改密码
     */
    public void changePassword(String oldPassword, String newPassword) {
        oldPassword = ValueUtility.requireNotBlank(oldPassword, "旧密码不能为空");
        newPassword = ValueUtility.requireNotBlank(newPassword, "新密码不能为空");

        if (!isPasswordMatch(oldPassword)) {
            throw new DomainException("密码错误");
        }

        this.password = CryptoUtility.md5Encode(newPassword);
        this.updatedAt = ValueUtility.getCurrentDateTime();
    }

    /**
     * 修改昵称
     */
    public void changeNickname(String nickname, UserUniquenessValidator userUniquenessValidator) {
        nickname = ValueUtility.requireNotBlank(nickname, "昵称不能为空");

        if (!nickname.equalsIgnoreCase(this.nickname) && userUniquenessValidator != null) {
            if (!userUniquenessValidator.isNicknameAvailable(nickname))
                throw new DomainException("昵称已存在");
        }

        this.nickname = nickname;
        this.updatedAt = ValueUtility.getCurrentDateTime();
    }

    /**
     * 修改手机
     */
    public void changeMobile(String mobile, UserUniquenessValidator userUniquenessValidator) {
        mobile = ValueUtility.requireNotBlank(mobile, "手机不能为空");

        if (!mobile.equalsIgnoreCase(this.mobile) && userUniquenessValidator != null) {
            if (!userUniquenessValidator.isMobileAvailable(mobile))
                throw new DomainException("手机已存在");
        }

        this.mobile = mobile;
        this.updatedAt = ValueUtility.getCurrentDateTime();
    }

    /**
     * 创建用户
     */
    public static User create(
            String id, String username, String password, String nickname, String mobile, boolean isAdmin,
            UserUniquenessValidator userUniquenessValidator) {
        id = ValueUtility.requireNotBlank(id, "用户 Id 不能为空");
        username = ValueUtility.requireNotBlank(username, "用户名不能为空");
        password = ValueUtility.requireNotBlank(password, "密码不能为空");
        nickname = ValueUtility.requireNotBlank(nickname, "昵称不能为空");
        mobile = ValueUtility.emptyIfNull(mobile);

        if (userUniquenessValidator != null) {
            if (!userUniquenessValidator.isUsernameAvailable(username))
                throw new DomainException("用户名已存在");

            if (!userUniquenessValidator.isNicknameAvailable(nickname))
                throw new DomainException("昵称已存在");

            if (!ValueUtility.isBlank(mobile) && !userUniquenessValidator.isMobileAvailable(mobile))
                throw new DomainException("手机已存在");
        }

        User user = new User();
        user.id = id;
        user.username = username;
        user.password = CryptoUtility.md5Encode(password);
        user.nickname = nickname;
        user.mobile = mobile;
        user.isAdmin = isAdmin;
        user.createdAt = ValueUtility.getCurrentDateTime();
        user.updatedAt = null;
        return user;
    }

    /**
     * 修改用户
     */
    public void update(
            String username, String nickname, String mobile, boolean isAdmin,
            UserUniquenessValidator userUniquenessValidator
    ) {
        username = ValueUtility.requireNotBlank(username, "用户名不能为空");
        nickname = ValueUtility.requireNotBlank(nickname, "昵称不能为空");
        mobile = ValueUtility.emptyIfNull(mobile);

        if (userUniquenessValidator != null) {
            if (!username.equalsIgnoreCase(this.username) && !userUniquenessValidator.isUsernameAvailable(username))
                throw new DomainException("用户名已存在");

            if (!nickname.equalsIgnoreCase(this.nickname) && !userUniquenessValidator.isNicknameAvailable(nickname))
                throw new DomainException("昵称已存在");

            if (!ValueUtility.isBlank(mobile) && !mobile.equalsIgnoreCase(this.mobile) &&
                    !userUniquenessValidator.isMobileAvailable(mobile))
                throw new DomainException("手机已存在");
        }

        this.username = username;
        this.nickname = nickname;
        this.mobile = mobile;
        this.isAdmin = isAdmin;
        this.updatedAt = ValueUtility.getCurrentDateTime();
    }
}
