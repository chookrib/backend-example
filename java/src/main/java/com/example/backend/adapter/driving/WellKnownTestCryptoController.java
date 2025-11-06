package com.example.backend.adapter.driving;

import com.example.backend.utility.CryptoUtility;
import com.example.backend.utility.JsonUtility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试 Crypto Well Known Controller
 */
@RestController
public class WellKnownTestCryptoController {

    private static final Logger logger = LoggerFactory.getLogger(WellKnownTestCryptoController.class);

    /**
     * JWT 编码
     */
    @RequestMapping(value = "/.well-known/test/crypto/jwt-encode", method = RequestMethod.POST)
    public Result testCryptoJwtEncode(@RequestBody String requestBody)
    {
        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        String secret = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "secret");
        LocalDateTime expiresAt = RequestValueHelper.getRequestJsonDateTimeReq(requestJson, "expiresAt");
        var payloadNode = requestJson.path("payload");
        // System.out.println("payloadNode: " + payloadNode.toString());
        Map<String, ?> payload = JsonUtility.deserialize(payloadNode, new TypeReference<Map<String, ?>>() { });
        // System.out.print("payload: " + JsonUtility.serialize(payload));
        String token = CryptoUtility.jwtEncode(payload, secret, expiresAt);
        return Result.okData(Map.of("token", token));
    }

    /**
     * JWT 解码
     */
    @RequestMapping(value = "/.well-known/test/crypto/jwt-decode", method = RequestMethod.POST)
    public Result testCryptoJwtDecode(@RequestBody String requestBody)
    {
        var requestJson = RequestValueHelper.getRequestJson(requestBody);
        String secret = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "secret");
        String token = RequestValueHelper.getRequestJsonStringTrimReq(requestJson, "token");
        Map<String, ?> payload = CryptoUtility.jwtDecode(token, secret);
        String payloadString = JsonUtility.serialize(payload);
        return Result.okData(Map.of("payload", payloadString));
    }

    /**
     * MD5 编码
     */
    @RequestMapping(value = "/.well-known/test/crypto/md5-encode", method = RequestMethod.GET)
    public Result testCryptoMd5Encode(HttpServletRequest request)
    {
        String text = RequestValueHelper.getRequestParamStringTrimReq(request, "text");
        String result = CryptoUtility.md5Encode(text);
        return Result.okData(Map.of("result", result));
    }
}


