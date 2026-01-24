package com.example.backend.domain;

/**
 * 用户唯一性 Specification 接口
 */
public interface UserUniqueSpecification {

    /**
     * 用户名是否唯一
     */
    boolean isUsernameUnique(String username);

    /**
     * 昵称是否唯一
     */
    boolean isNicknameUnique(String nickname);

    /**
     * 手机号是否唯一
     */
    boolean isMobileUnique(String mobile);
}
