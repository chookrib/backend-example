package com.example.ddd.domain;

import java.util.List;

/**
 * 用户 Repository 接口
 */
public interface UserRepository {

    /**
     * 插入
     */
    void insert(User entity);

    /**
     * 更新
     */
    void update(User entity);

    /**
     * 根据 id 删除
     */
    void deleteById(String id);

    /**
     * 根据 id 查询，找不到返回 null
     */
    User selectById(String id);

    /**
     * 根据 id 查询，找不到抛出异常
     */
    User selectByIdReq(String id);

    /**
     * 根据 id 集合查询
     */
    List<User> selectByIds(List<String> ids);

    /**
     * 根据用户名查询，找不到返回 null
     */
    User selectByUsername(String username);
}

