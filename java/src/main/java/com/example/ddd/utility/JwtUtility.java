package com.example.ddd.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtility {
    /**
     * 编码JWT令牌
     */
    public static String encode(Map<String, ?> payload, Date expiresAt, String algorithm) {
        return JWT.create()
                //.withIssuer("")
                //.withSubject("")
                //.withIssuedAt(new Date())
                //.withClaim("id", id)
                .withPayload(payload)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(algorithm));
    }

    /**
     * 解析JWT令牌
     */
    public static Map<String, Claim> decode(String jwt, String algorithm) {
        DecodedJWT decodedJWT =  JWT.require(Algorithm.HMAC256(algorithm))
                .build()
                .verify(jwt);
        return decodedJWT.getClaims();
    }
}
