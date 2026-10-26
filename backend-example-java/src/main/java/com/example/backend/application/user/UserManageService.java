package com.example.backend.application.user;

import com.example.backend.application.ApplicationException;
import com.example.backend.application.IdGenerator;
import com.example.backend.application.lock.LockKeys;
import com.example.backend.application.lock.LockService;
import com.example.backend.domain.user.User;
import com.example.backend.domain.user.UserRepository;
import com.example.backend.domain.user.UserUniqueSpecification;
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
        User user = userRepository.selectByIdReq(id);
        user.setAdmin(isAdmin);
        userRepository.update(user);
    }

    /**
     * 创建用户
     */
    public String createUser(String username, String password, String nickname, String mobile) {
        try(AutoCloseable lock = lockService.lock(LockKeys.USER)){
            User user = User.create(
                    IdGenerator.generateId(), username, password, nickname, mobile, this.userUniqueSpecification
            );
            userRepository.insert(user);
            return user.getId();
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * 修改用户
     */
    public void modifyUser(String id, String username, String nickname, String mobile) {
        try(AutoCloseable lock = lockService.lock(LockKeys.USER)){
            User user = userRepository.selectByIdReq(id);
            user.modify(username, nickname, mobile, userUniqueSpecification);
            userRepository.update(user);
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * 删除用户
     */
    public void removeUser(String id) {
        userRepository.deleteById(id);
    }
}
