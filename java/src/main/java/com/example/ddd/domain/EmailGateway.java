package com.example.ddd.domain;

/**
 * 邮件接口
 */
public interface EmailGateway {
    /**
     * 发送邮件
     */
    void sendEmail(String to, String subject, String body);
}
