package com.example.backend.application;

import com.example.backend.utility.ValueUtility;

import java.util.*;

/**
 * 排序条件构造器
 */
public class QuerySortBuilder {

    public static final String ORDER_ASC = "ASC";
    public static final String ORDER_DESC = "DESC";
    public static final List<String> VALID_ORDERS  = List.of(ORDER_ASC, ORDER_DESC);

    private final Map<String, String> allowedFieldMap = new HashMap<>();
    private final List<SortItem> sortItems = new ArrayList<>();

    public QuerySortBuilder(List<String> allowedFields) {
        if (allowedFields == null || allowedFields.isEmpty())
            throw new ApplicationException("必须指定有效的排序字段集合");

        for (String field : allowedFields) {
            this.allowedFieldMap.put(field.toLowerCase(), field);
        }
    }

    /**
     * 添加排序项，可链式调用
     */
    public QuerySortBuilder addSort(String field, String order) {
        if (allowedFieldMap.isEmpty())
            return this;

        if (ValueUtility.isEmptyString(field) || ValueUtility.isEmptyString(order))
            return this;

        final String fieldKey = field.trim().toLowerCase();
        if (!allowedFieldMap.containsKey(fieldKey))
            return this;

        order = order.trim().toUpperCase();
        if (!VALID_ORDERS.contains(order))
            return this;

        // 排序字段已存在时跳过
        if(sortItems.stream().anyMatch(s -> s.field.equalsIgnoreCase(fieldKey)))
            return this;

        sortItems.add(new SortItem(allowedFieldMap.get(fieldKey), order));

        return this;
    }

    /**
     * 取排序项
     */
    public List<SortItem> build() {
        return List.copyOf(sortItems);
    }

    /**
     * 单条排序项
     */
    public record SortItem(String field, String order) {

    }
}
