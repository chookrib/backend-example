package com.example.ddd.domain;

import java.util.List;

/**
 * 用户Repository接口
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
     * 根据Id删除
     */
    void deleteById(String id);

    /**
     * 根据Id查询，找不到返回null
     */
    User selectById(String id);

    /**
     * 根据Id查询，找不到抛出异常
     */
    User selectByIdReq(String id);

    /**
     * 根据Id集合查询
     */
    List<User> selectByIds(List<String> ids);

    /**
     * 根据用户名查询，找不到返回null
     */
    User selectByUsername(String username);
}

