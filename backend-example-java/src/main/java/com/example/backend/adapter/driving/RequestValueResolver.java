package com.example.backend.adapter.driving;

import com.example.backend.utility.JsonExtractor;
import com.example.backend.utility.ValueUtility;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;

/**
 * 请求 Value Resolver
 */
public final class RequestValueResolver {

    /**
     * 构造 JSON Extractor
     */
    public static JsonExtractor buildJsonExtractor(String requestBody) {
        try {
            return new JsonExtractor(requestBody);
        } catch (Exception ex) {
            throw new ControllerException("请求体不是合法的 JSON");
        }
    }

    //==================================================================================================================

    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 规范化页码
     */
    public static int normalizePageNum(String pageNum) {
        int pn = ValueUtility.toIntOrDefault(pageNum, DEFAULT_PAGE_NUM);
        return pn >= 1 ? pn : DEFAULT_PAGE_NUM;
    }

    /**
     * 规范化页大小
     */
    public static int normalizePageSize(String pageSize) {
        int ps = ValueUtility.toIntOrDefault(pageSize, DEFAULT_PAGE_SIZE);
        return ps >= 1 && ps <= 100 ? ps : DEFAULT_PAGE_SIZE;
    }

    /**
     * 分页参数
     */
    public record Paging(int pageNum, int pageSize, int totalCount) { }

    /**
     * 验证并修正分页参数
     */
    public static Paging normalizePaging(int pageNum, int pageSize, int totalCount) {
        if (pageSize < 1)
            pageSize = 1;
        if (totalCount < 0)
            totalCount = 0;

        int maxPageNum = totalCount / pageSize;
        if (totalCount % pageSize > 0)
            maxPageNum++;

        if (pageNum > maxPageNum)
            pageNum = maxPageNum;
        if (pageNum < 1)
            pageNum = 1;

        return new Paging(pageNum, pageSize, totalCount);
    }

    //==================================================================================================================

    /**
     * 取排序参数，返回值为有序 Map
     */
    public static LinkedHashMap<String, String> getSortOptions(JsonNode jsonNode) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (jsonNode == null || jsonNode.isMissingNode() || !jsonNode.isArray())
            return map;

        for (JsonNode n : jsonNode) {
            String field = n.get("field").asText();
            String order = n.get("order").asText();

            if (ValueUtility.isBlank(field))
                throw new ControllerException("排序字段不能为空");

            if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))
                throw new ControllerException("排序方向只能为 asc 或 desc");

            map.put(field, order.toUpperCase());
        }
        return map;
    }
}
