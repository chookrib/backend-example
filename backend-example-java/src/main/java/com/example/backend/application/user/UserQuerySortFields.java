package com.example.backend.application.user;

import java.util.List;

/**
 * 用户排序字段
 */
public class UserQuerySortFields {

    public static final String CREATED_AT = "createdAt";     // 创建时间
    public static final String USERNAME = "username";       // 用户名

    public static final List<String> ALL = List.of(CREATED_AT, USERNAME);
}
