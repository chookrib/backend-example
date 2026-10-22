package com.example.backend.application.user;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户 Query Criteria
 */
public class UserQueryCriteria {

    // 查询条件

    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    // 排序条件

    private Map<String, String> sortMap = new LinkedHashMap<>();

    public void addSort(String field, String order) {
        sortMap.put(field, order);
    }
}
