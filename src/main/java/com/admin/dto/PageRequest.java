package com.admin.dto;

import lombok.Data;

/**
 * 分页请求参数
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
public class PageRequest {
    /**
     * 当前页码，从1开始
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向：ASC 或 DESC
     */
    private String sortOrder = "DESC";

    /**
     * 获取偏移量
     *
     * @return 偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}


