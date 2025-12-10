package com.admin.util;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 *
 * @param <T> 数据类型
 * @author Admin
 * @date 2024-01-01
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 分页数据
     */
    private PageData<T> data;

    public PageResult() {
    }

    public PageResult(Integer code, String message, PageData<T> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> PageResult<T> success(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        PageData<T> pageData = new PageData<>();
        pageData.setList(list);
        pageData.setTotal(total);
        pageData.setPageNum(pageNum);
        pageData.setPageSize(pageSize);
        return new PageResult<>(200, "查询成功", pageData);
    }

    /**
     * 分页数据内部类
     */
    public static class PageData<T> implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 数据列表
         */
        private List<T> list;

        /**
         * 总记录数
         */
        private Long total;

        /**
         * 当前页码
         */
        private Integer pageNum;

        /**
         * 每页大小
         */
        private Integer pageSize;

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Integer getPageNum() {
            return pageNum;
        }

        public void setPageNum(Integer pageNum) {
            this.pageNum = pageNum;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }
    }
}

