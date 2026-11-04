package com.example.backend.application.user;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 用户 Query Handler 接口
 */
public interface UserQueryHandler {

    /**
     * 根据 id 查询，找不到返回 null
     */
    UserDto queryById(String id);

    /**
     * 根据 id 查询，找不到抛出异常
     */
    UserDto queryByIdRequired(String id);

    /**
     * 查询记录数
     */
    int queryCount(UserQueryCriteria criteria);

    /**
     * 查询
     */
    List<UserDto> query(UserQueryCriteria criteria, LinkedHashMap<String, String> sortOptions);

    /**
     * 分页查询
     */
    List<UserDto> queryPage(int pageNum, int pageSize, UserQueryCriteria criteria, LinkedHashMap<String, String> sortOptions);
}
