package com.example.backend.application.user;

import com.example.backend.utility.IdentifierUtility;
import com.example.backend.application.lock.LockHandle;
import com.example.backend.application.lock.LockKeyConstants;
import com.example.backend.application.lock.LockService;
import com.example.backend.domain.user.User;
import com.example.backend.domain.user.UserRepository;
import com.example.backend.domain.user.UserUniquenessValidator;
import org.springframework.stereotype.Component;

/**
 * 用户管理 Service
 */
@Component
public class UserManageService {

    private final UserRepository userRepository;
    private final UserUniquenessValidator userUniquenessValidator;
    private final LockService lockService;

    public UserManageService(
            UserRepository userRepository,
            UserUniquenessValidator userUniquenessValidator,
            LockService lockService
    ) {
        this.userRepository = userRepository;
        this.userUniquenessValidator = userUniquenessValidator;
        this.lockService = lockService;
    }

    /**
     * 设置用户管理员状态
     */
    public void setAdmin(String id, boolean isAdmin) {
        User user = userRepository.findByIdOrThrow(id);
        user.setAdmin(isAdmin);
        userRepository.update(user);
    }

    /**
     * 创建用户
     */
    public String createUser(String username, String password, String nickname, String mobile, boolean isAdmin) {
        try (LockHandle lock = lockService.lock(LockKeyConstants.USER)) {
            User user = User.create(
                    IdentifierUtility.generateUUIDv7(), username, password, nickname, mobile, isAdmin,
                    this.userUniquenessValidator
            );
            userRepository.insert(user);
            return user.getId();
        }
    }

    /**
     * 修改用户
     */
    public void updateUser(String id, String username, String nickname, String mobile, boolean isAdmin) {
        try (LockHandle lock = lockService.lock(LockKeyConstants.USER)) {
            User user = userRepository.findByIdOrThrow(id);
            user.update(username, nickname, mobile, isAdmin, userUniquenessValidator);
            userRepository.update(user);
        }
    }

    /**
     * 删除用户
     */
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
