package com.example.ddd.domain;

/**
 * 短信 Gateway 接口
 */
public interface SmsGateway {

    /**
     * 发送验证码
     */
    void sendCode(String mobile, String code);
}
