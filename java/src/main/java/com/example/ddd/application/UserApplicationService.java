package com.example.ddd.application;

import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import org.springframework.stereotype.Service;

/**
 * 用户应用服务
 */
@Service
public class UserApplicationService {

    private final UserRepository userRepository;

    public UserApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 注册
     */
    public String register(String username, String password, String nickname, String mobile, String email) {
        User user = User.createUser(username, password, nickname, mobile, email);
        userRepository.insert(user);
        return user.getId();
    }

    /**
     * 登录
     */
    public String login(String username, String password) {
        User user = userRepository.selectByUsername(username);
        return user.login(password);
    }
}
