package com.example.ddd.application;

/**
 * 分页信息
 */
public class PageInfoDto {

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
    public static PageInfoDto validation(int pageNum, int pageSize, int totalCount) {
        PageInfoDto pageInfo = new PageInfoDto();
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
