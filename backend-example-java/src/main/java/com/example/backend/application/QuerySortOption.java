package com.example.backend.application;

import com.example.backend.utility.ValueUtility;

/**
 * 排序项
 */
public class QuerySortOption {

    private final String field;
    private final String order;

    public QuerySortOption(String field, String order) {
        if (ValueUtility.isEmptyString(field))
            throw new ApplicationException("排序字段不能为空");

        if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))
            throw new ApplicationException("排序方向只能为 asc 或 desc");

        this.field = field;
        this.order = order.toUpperCase();
    }

    public String getField() {
        return field;
    }

    public String getOrder() {
        return order;
    }
}