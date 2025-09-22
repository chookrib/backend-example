package com.example.ddd.application;

import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 用户管理服务
 */
@Component
public class UserManageService {

    private final UserRepository userRepository;

    public UserManageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 设置用户管理员状态
     */
    public void setAdmin(String userId, boolean isAdmin) {
        User user = userRepository.selectByIdReq(userId);
        user.setAdmin(isAdmin);
        userRepository.update(user);
    }
}
