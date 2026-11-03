package com.example.backend.domain.user;

/**
 * 用户唯一性 Validator 接口
 */
public interface UserUniquenessValidator {

    /**
     * 用户名是否可用
     */
    boolean isUsernameAvailable(String username);

    /**
     * 昵称是否可用
     */
    boolean isNicknameAvailable(String nickname);

    /**
     * 手机号是否可用
     */
    boolean isMobileAvailable(String mobile);
}
