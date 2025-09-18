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
     * 删除
     */
    void deleteById(String id);

    /**
     * 根据id查询，找不到返回null
     */
    User selectById(String id);

    /**
     * 根据id查询，找不到抛出异常
     */
    User selectByIdReq(String id);

    /**
     * 根据id集合查询
     */
    List<User> selectByIds(List<String> ids);

    /**
     * 查询记录数
     */
    int selectCount(UserCriteria criteria);

    /**
     * 查询
     */
    List<User> select(UserCriteria criteria);

    /**
     * 分页查询，pageIndex从1开始
     */
    List<User> selectByPage(int pageIndex, int pageSize, UserCriteria criteria);

    // =================================================================================================================

    /**
     * 根据用户名查询，找不到返回null
     */
    User selectByUsername(String username);

}

