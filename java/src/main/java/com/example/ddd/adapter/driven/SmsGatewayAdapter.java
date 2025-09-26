package com.example.ddd.adapter.driven;

import com.example.ddd.domain.SmsGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 短信 Gateway 接口 Adapter
 */
@Component
public class SmsGatewayAdapter implements SmsGateway {

    private static final Logger logger = LoggerFactory.getLogger(SmsGatewayAdapter.class);

    @Override
    public void sendCode(String mobile, String code) {
        logger.info("发送手机验证码到 {}, code: {}", mobile, code);
    }
}
