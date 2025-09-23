package com.example.ddd.application;

import java.util.List;

/**
 * 用户查询Handler接口
 */
public interface UserQueryHandler {
    /**
     * 根据id查询，找不到返回null
     */
    UserDto queryById(String id);

    /**
     * 根据id查询，找不到抛出异常
     */
    UserDto queryByIdReq(String id);

    /**
     * 查询记录数
     */
    int queryCount(UserQueryCriteria criteria);

    /**
     * 查询
     */
    List<UserDto> query(UserQueryCriteria criteria, UserQuerySort... sorts);

    /**
     * 分页查询
     */
    List<UserDto> queryByPage(int pageNum, int pageSize, UserQueryCriteria criteria, UserQuerySort... sorts);
}
