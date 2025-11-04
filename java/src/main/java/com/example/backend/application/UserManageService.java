package com.example.backend.application;

import com.example.backend.application.lock.LockKeys;
import com.example.backend.application.lock.LockService;
import com.example.backend.domain.User;
import com.example.backend.domain.UserRepository;
import com.example.backend.domain.UserUniqueSpecification;
import org.springframework.stereotype.Component;

/**
 * 用户管理 Service
 */
@Component
public class UserManageService {

    private final UserRepository userRepository;
    private final UserUniqueSpecification userUniqueSpecification;
    private final LockService lockService;

    public UserManageService(
            UserRepository userRepository,
            UserUniqueSpecification userUniqueSpecification,
            LockService lockService
    ) {
        this.userRepository = userRepository;
        this.userUniqueSpecification = userUniqueSpecification;
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
        try(AutoCloseable lock = this.lockService.lock(LockKeys.USER)){
            User user = User.createUser(
                    IdGenerator.generateId(), username, password, nickname, mobile, this.userUniqueSpecification
            );
            this.userRepository.insert(user);
            return user.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改用户
     */
    public void modifyUser(String id, String username, String nickname, String mobile) {
        try(AutoCloseable lock = this.lockService.lock(LockKeys.USER)){
            User user = this.userRepository.selectByIdReq(id);
            user.modify(username, nickname, mobile, this.userUniqueSpecification);
            this.userRepository.update(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除用户
     */
    public void removeUser(String id) {
        this.userRepository.deleteById(id);
    }
}
