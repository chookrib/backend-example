package com.example.ddd.application;

import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.UserUniqueChecker;
import org.springframework.stereotype.Component;

/**
 * 用户管理服务
 */
@Component
public class UserManageService {

    private final UserRepository userRepository;
    private final UserUniqueChecker userUniqueChecker;

    public UserManageService(UserRepository userRepository, UserUniqueChecker userUniqueChecker) {
        this.userRepository = userRepository;
        this.userUniqueChecker = userUniqueChecker;
    }

    /**
     * 设置用户管理员状态
     */
    public void setAdmin(String userId, boolean isAdmin) {
        User user = userRepository.selectByIdReq(userId);
        user.setAdmin(isAdmin);
        userRepository.update(user);
    }

    /**
     * 创建用户信息，管理员操作
     */
    public String createUser(String username, String password, String nickname, String mobile) {
        User user = User.createUser(username, password, nickname, mobile, userUniqueChecker);
        userRepository.insert(user);
        return user.getId();
    }

    /**
     * 修改用户信息，管理员操作
     */
    public void modifyUser(String id, String username, String nickname, String mobile) {
        User user = userRepository.selectByIdReq(id);
        user.modify(username, nickname, mobile, userUniqueChecker);
        userRepository.update(user);
    }

    /**
     * 删除用户，管理员操作
     */
    public void removeUser(String id) {
        userRepository.deleteById(id);
    }
}
