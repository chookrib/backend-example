package com.example.ddd.application;

import com.example.ddd.domain.User;
import com.example.ddd.utility.JacksonUtility;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 用户数据传输对象
 */
public class UserDto {

    /**
     * 将用户对象转换为JsonNode
     */
    public static JsonNode fromUser(User user){

        return JacksonUtility.readTree(JacksonUtility.writeValueAsString(user));
    }
}
