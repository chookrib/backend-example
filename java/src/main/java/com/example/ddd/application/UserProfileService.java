package com.example.ddd.application;

import com.example.ddd.domain.SmsGateway;
import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.UserUniqueChecker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 用户资料服务
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
     * 注册
     * 仅演示使用，不具备防止恶意注册功能
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
     * 发送绑定手机验证码
     */
    public void sendMobileCode(String userId, String mobile) {
        if(StringUtils.isBlank(mobile))
            throw new ApplicationException("手机号不能为空");

        User user = userRepository.selectByIdReq(userId);
        String code = String.format("%04d", (int)(Math.random() * 10000));
        mobileCodeMap.put(user.getId() + "_" + mobile, code);
        smsGateway.sendCode(mobile, code);
    }

    /**
     * 修改手机
     */
    public void bindMobile(String userId, String mobile, String code)  {
        if(StringUtils.isBlank(code))
            throw new ApplicationException("验证码不能为空");

        User user = userRepository.selectByIdReq(userId);
        if(!mobileCodeMap.getOrDefault(user.getId() + "_" + mobile, "").equals(code))
            throw new ApplicationException("手机验证码错误");

        user.modifyMobile(mobile, userUniqueChecker);
        userRepository.update(user);
    }
}
