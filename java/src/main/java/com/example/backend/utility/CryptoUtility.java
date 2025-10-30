package com.example.backend.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

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
    public static String encodeJwt(
            // Map<String, ?> payload,
            Map<String, String> payload, Date expiresAt, String secret) {
        return JWT.create()
                .withPayload(payload)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * JWT 解码
     */
    public static Map<String, String> decodeJwt(String jwt, String secret) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(jwt);
        //return decodedJWT.getClaims();        // return Map<String, Claim>
        Map<String, Claim> claims = decodedJWT.getClaims();
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Claim> entry : claims.entrySet()) {
            result.put(entry.getKey(), entry.getValue().asString());
        }
        return result;
    }

    /**
     * MD5 编码
     */
    public static String encodeMd5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
