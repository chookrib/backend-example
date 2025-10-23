package com.example.ddd.application;

import com.example.ddd.application.lock.LockKeys;
import com.example.ddd.application.lock.LockService;
import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.UserUniqueChecker;
import org.springframework.stereotype.Component;

/**
 * 用户管理 Service
 */
@Component
public class UserManageService {

    private final UserRepository userRepository;
    private final UserUniqueChecker userUniqueChecker;
    private final LockService lockService;

    public UserManageService(
            UserRepository userRepository,
            UserUniqueChecker userUniqueChecker,
            LockService lockService
    ) {
        this.userRepository = userRepository;
        this.userUniqueChecker = userUniqueChecker;
        this.lockService = lockService;
    }

    /**
     * 设置用户管理员状态
     */
    public void setAdmin(String id, boolean isAdmin) {
        User user = this.userRepository.selectByIdReq(id);
        user.setAdmin(isAdmin);
        this.userRepository.update(user);
    }

    /**
     * 创建用户
     */
    public String createUser(String username, String password, String nickname, String mobile) {
        return this.lockService.executeWithLock(LockKeys.USER, () -> {
            User user = User.createUser(
                    IdGenerator.generateId(), username, password, nickname, mobile, this.userUniqueChecker
            );
            this.userRepository.insert(user);
            return user.getId();
        });
    }

    /**
     * 修改用户
     */
    public void modifyUser(String id, String username, String nickname, String mobile) {
        this.lockService.executeWithLock(LockKeys.USER, () -> {
            User user = this.userRepository.selectByIdReq(id);
            user.modify(username, nickname, mobile, this.userUniqueChecker);
            this.userRepository.update(user);
        });
    }

    /**
     * 删除用户
     */
    public void removeUser(String id) {
        this.userRepository.deleteById(id);
    }
}
