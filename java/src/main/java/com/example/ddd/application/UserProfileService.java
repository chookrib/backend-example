package com.example.ddd.application;

import com.example.ddd.domain.SmsGateway;
import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.UserUniqueChecker;
import com.example.ddd.utility.ValueUtility;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 用户资料Service
 */
@Component
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserUniqueChecker userUniqueChecker;
    private final SmsGateway smsGateway;

    private HashMap<String, String> mobileCodeMap = new HashMap<>();    // 手机验证码缓存，无过期策略，仅供演示使用

    public UserProfileService(
            UserRepository userRepository, UserUniqueChecker userUniqueChecker, SmsGateway smsGateway) {
        this.userRepository = userRepository;
        this.userUniqueChecker = userUniqueChecker;
        this.smsGateway = smsGateway;
    }

    /**
     * 注册，仅演示使用，未防止恶意注册功能
     */
    public String register(String username, String password, String nickname) {
        User user = User.registerUser(IdGenerator.generateId(), username, password, nickname, userUniqueChecker);
        userRepository.insert(user);
        return user.getId();
    }

    /**
     * 修改密码
     */
    public void modifyPassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.selectByIdReq(userId);
        user.modifyPassword(oldPassword, newPassword);
        userRepository.update(user);
    }

    /**
     * 修改昵称
     */
    public void modifyNickname(String userId, String nickname) {
        // 可在此添加用户名修改次数限制
        User user = userRepository.selectByIdReq(userId);
        user.modifyNickname(nickname, userUniqueChecker);
        userRepository.update(user);
    }

    /**
     * 发送手机验证码
     */
    public void sendMobileCode(String userId, String mobile) {
        if (ValueUtility.isBlank(mobile))
            throw new ApplicationException("手机号不能为空");

        User user = userRepository.selectByIdReq(userId);
        String code = String.format("%06d", (int) (Math.random() * 1000000));
        mobileCodeMap.put(user.getId() + "_" + mobile, code);
        smsGateway.sendCode(mobile, code);
    }

    /**
     * 绑定手机
     */
    public void bindMobile(String userId, String mobile, String code) {
        if (ValueUtility.isBlank(code))
            throw new ApplicationException("验证码不能为空");

        User user = userRepository.selectByIdReq(userId);
        String key = user.getId() + "_" + mobile;
        if (!mobileCodeMap.getOrDefault(key, "").equals(code))
            throw new ApplicationException("手机验证码错误");

        user.modifyMobile(mobile, userUniqueChecker);
        userRepository.update(user);
        mobileCodeMap.remove(key);
    }
}
