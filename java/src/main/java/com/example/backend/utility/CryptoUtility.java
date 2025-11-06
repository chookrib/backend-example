package com.example.backend.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 加密 Utility
 */
public class CryptoUtility {

    /**
     * JWT 编码
     */
    public static String jwtEncode(Map<String, ?> payload, String secret, LocalDateTime expiresAt) {
        Date expiresAtDate = Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withPayload(payload)
                .withExpiresAt(expiresAtDate)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * JWT 解码
     */
    public static Map<String, ?> jwtDecode(String token, String secret) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            //return decodedJWT.getClaims();        // Map<String, Claim>
            Map<String, Claim> payloadClaims = decodedJWT.getClaims();
            Map<String, Object> payload = new HashMap<>();
            for (Map.Entry<String, Claim> entry : payloadClaims.entrySet()) {
                payload.put(entry.getKey(), entry.getValue().as(Object.class));
            }
            return payload;
        }
        catch (Exception ex) {
            throw new UtilityException("JWT 解码失败", ex);
        }
    }

    /**
     * JWT 计算过期时长（分钟）
     */
    public static int jwtExpiresMinute(String expires) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^(\\d+)([dhm])$")
                .matcher(expires.toLowerCase());
        if (!matcher.matches()) {
            throw new UtilityException("JWT EXPIRES 配置错误，值应为时长（正整数）加时长单位（d/h/m）");
        }
        int num;
        try {
            num = Integer.parseInt(matcher.group(1));
        } catch (Exception ex) {
            throw new UtilityException("JWT EXPIRES 配置错误，时长应为正整数", ex);
        }
        String unit = matcher.group(2);
        if ("d".equals(unit)) {
            return num * 24 * 60;
        }
        if ("h".equals(unit)) {
            return num * 60;
        }
        return num;
    }

    /**
     * MD5 编码
     */
    public static String md5Encode(String text) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new UtilityException("MD5 编码异常", ex);
        }
    }
}
