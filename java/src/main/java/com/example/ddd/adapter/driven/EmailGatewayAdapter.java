package com.example.ddd.adapter.driven;

import com.example.ddd.domain.EmailGateway;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 邮件接口实现适配器
 */
@Component
public class EmailGatewayAdapter implements EmailGateway {

    private static final Logger logger = LoggerFactory.getLogger(EmailGatewayAdapter.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        if(StringUtils.isBlank(to))
            logger.warn("Email to address is blank. subject: {}, body: {}", subject, body);

        logger.info("Sending email to {}, subject: {}, body: {}", to, subject, body);
    }
}
