package com.example.ddd.domain;

/**
 * 用户唯一性检查接口
 */
public interface UserUniqueChecker {

    /**
     * 检查用户名是否唯一
     */
    boolean isUsernameUnique(String username);

    /**
     * 检查昵称是否唯一
     */
    boolean isNicknameUnique(String nickname);

    /**
     * 检查手机号是否唯一
     */
    boolean isMobileUnique(String mobile);
}
