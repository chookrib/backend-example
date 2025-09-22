package com.example.ddd.adapter.driving;

/**
 * 分页验证器
 */
public class PageInfoValidator {

    private int pageNum;
    private int pageSize;
    private int totalCount;

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 验证并修正分页参数
     */
    public static PageInfoValidator validation(int pageNum, int pageSize, int totalCount) {
        PageInfoValidator pageInfo = new PageInfoValidator();
        pageInfo.pageNum = pageNum;
        pageInfo.pageSize = pageSize;
        pageInfo.totalCount = totalCount;

        if(pageInfo.pageSize < 1)
            pageInfo.pageSize = 1;
        if(pageInfo.totalCount < 0)
            pageInfo.totalCount = 0;

        int maxPageNum = pageInfo.totalCount / pageInfo.pageSize;
        if(pageInfo.totalCount % pageInfo.pageSize > 0)
            maxPageNum++;

        if(pageInfo.pageNum > maxPageNum)
            pageInfo.pageNum = maxPageNum;
        if(pageInfo.pageNum < 1)
            pageInfo.pageNum = 1;
        return pageInfo;
    }
}
