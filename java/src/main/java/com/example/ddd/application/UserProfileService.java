package com.example.ddd.application;

import com.example.ddd.application.lock.LockKeys;
import com.example.ddd.application.lock.LockService;
import com.example.ddd.domain.SmsGateway;
import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.UserUniqueSpecification;
import com.example.ddd.utility.ValueUtility;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户资料 Service
 */
@Component
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserUniqueSpecification userUniqueSpecification;
    private final SmsGateway smsGateway;
    private final LockService lockService;

    private ConcurrentHashMap<String, String> mobileCodeMap = new ConcurrentHashMap<>();    // 手机验证码缓存，无过期策略，仅供演示使用

    public UserProfileService(
            UserRepository userRepository,
            UserUniqueSpecification userUniqueSpecification,
            SmsGateway smsGateway,
            LockService lockService
    ) {
        this.userRepository = userRepository;
        this.userUniqueSpecification = userUniqueSpecification;
        this.smsGateway = smsGateway;
        this.lockService = lockService;
    }

    /**
     * 注册，仅演示使用，未防止恶意注册功能
     */
    public String register(String username, String password, String nickname) {
        return this.lockService.getWithLock(LockKeys.USER, () -> {
            User user = User.registerUser(IdGenerator.generateId(), username, password, nickname, this.userUniqueSpecification);
            this.userRepository.insert(user);
            return user.getId();
        });
    }

    /**
     * 修改密码
     */
    public void modifyPassword(String userId, String oldPassword, String newPassword) {
        User user = this.userRepository.selectByIdReq(userId);
        user.modifyPassword(oldPassword, newPassword);
        this.userRepository.update(user);
    }

    /**
     * 修改昵称
     */
    public void modifyNickname(String userId, String nickname) {
        this.lockService.runWithLock(LockKeys.USER, () -> {
            User user = this.userRepository.selectByIdReq(userId);
            user.modifyNickname(nickname, this.userUniqueSpecification);
            this.userRepository.update(user);
        });
    }

    /**
     * 发送手机验证码
     */
    public void sendMobileCode(String userId, String mobile) {
        if (ValueUtility.isBlank(mobile))
            throw new ApplicationException("手机号不能为空");

        User user = this.userRepository.selectByIdReq(userId);
        String code = String.format("%06d", (int) (Math.random() * 1000000));
        this.mobileCodeMap.put(user.getId() + "_" + mobile, code);
        this.smsGateway.sendCode(mobile, code);
    }

    /**
     * 绑定手机
     */
    public void bindMobile(String userId, String mobile, String code) {
        if (ValueUtility.isBlank(code))
            throw new ApplicationException("验证码不能为空");

        User user = this.userRepository.selectByIdReq(userId);
        String key = user.getId() + "_" + mobile;
        if (!this.mobileCodeMap.getOrDefault(key, "").equals(code))
            throw new ApplicationException("验证码错误");

        user.modifyMobile(mobile, this.userUniqueSpecification);
        this.userRepository.update(user);
        this.mobileCodeMap.remove(key);
    }
}
