package com.example.backend.adapter.driving.test;

import com.example.backend.adapter.driving.ControllerException;
import com.example.backend.adapter.driving.RequestValueResolver;
import com.example.backend.adapter.driving.Result;
import com.example.backend.utility.CryptoUtility;
import com.example.backend.utility.JsonExtractor;
import com.example.backend.utility.JsonUtility;
import com.example.backend.utility.ValueUtility;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 测试加解密 Controller
 */
@RestController
public class TestCryptoController {

    private static final Logger logger = LoggerFactory.getLogger(TestCryptoController.class);

    /**
     * JWT 编码
     */
    @RequestMapping(value = "/api/test/crypto/jwt-encode", method = RequestMethod.POST)
    public Result testCryptoJwtEncode(@RequestBody String requestBody) {
        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String secret = jsonExtractor.getNotBlankStringOrThrow("secret", "密钥不能为空");
        LocalDateTime expiresAt = jsonExtractor.getDateTimeOrThrow("expiresAt", "到期时间无效");
        var payloadNode = jsonExtractor.getNode("payload");
        // System.out.println("payloadNode: " + payloadNode.toString());
        Map<String, ?> payload = null;
        try {
            payload = JsonUtility.deserialize(payloadNode, new TypeReference<Map<String, ?>>() { });
        } catch (Exception ex) {
            throw new ControllerException("payload 无效");
        }
        // System.out.print("payload: " + JsonUtility.serialize(payload));
        String token = CryptoUtility.jwtEncode(payload, secret, expiresAt);
        return Result.okData(Map.of("token", token));
    }

    /**
     * JWT 解码
     */
    @RequestMapping(value = "/api/test/crypto/jwt-decode", method = RequestMethod.POST)
    public Result testCryptoJwtDecode(@RequestBody String requestBody) {
        JsonExtractor jsonExtractor = RequestValueResolver.buildJsonExtractor(requestBody);
        String secret = jsonExtractor.getNotBlankStringOrThrow("secret", "密钥不能为空");
        String token = jsonExtractor.getNotBlankStringOrThrow("token", "令牌不能为空");
        Map<String, ?> payload = CryptoUtility.jwtDecode(token, secret);

        String headerDecoded = "";
        String payloadDecoded = "";
        String[] tokenParts = token.split("\\.");
        if (tokenParts.length > 1) {
            headerDecoded = CryptoUtility.base64Decode(tokenParts[0]);
        }
        if (tokenParts.length > 2) {
            payloadDecoded = CryptoUtility.base64Decode(tokenParts[1]);
        }

        return Result.okData(Map.of(
                "payload", JsonUtility.serialize(payload),
                "headerDecoded", headerDecoded,
                "payloadDecoded", payloadDecoded
        ));
    }

    /**
     * BASE64 编码
     */
    @RequestMapping(value = "/api/test/crypto/base64-encode", method = RequestMethod.GET)
    public Result testCryptoBase64Encode(HttpServletRequest request) {
        String text = ValueUtility.emptyIfNull(request.getParameter("text"));
        String base64 = CryptoUtility.base64Encode(text);
        return Result.okData(Map.of("base64", base64));
    }

    /**
     * BASE64 解码
     */
    @RequestMapping(value = "/api/test/crypto/base64-decode", method = RequestMethod.GET)
    public Result testCryptoBase64Decode(HttpServletRequest request) {
        String base64 = ValueUtility.emptyIfNull(request.getParameter("base64"));
        String text = CryptoUtility.base64Decode(base64);
        return Result.okData(Map.of("text", text));
    }

    /**
     * MD5 编码
     */
    @RequestMapping(value = "/api/test/crypto/md5-encode", method = RequestMethod.GET)
    public Result testCryptoMd5Encode(HttpServletRequest request) {
        String text = ValueUtility.emptyIfNull(request.getParameter("text"));
        String md5 = CryptoUtility.md5Encode(text);
        return Result.okData(Map.of("md5", md5));
    }
}


