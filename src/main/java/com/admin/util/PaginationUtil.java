package com.admin.util;

import com.admin.util.PageResult.PageData;

/**
 * 分页工具类
 * 提供分页相关的计算和判断方法
 * 
 * @author Admin
 * @date 2024-01-01
 */
public class PaginationUtil {

    /**
     * 计算总页数
     *
     * @param total    总记录数
     * @param pageSize 每页大小
     * @return 总页数
     */
    public static int calculateTotalPages(long total, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 判断是否有上一页
     *
     * @param currentPage 当前页码
     * @return 是否有上一页
     */
    public static boolean hasPrevPage(int currentPage) {
        return currentPage > 1;
    }

    /**
     * 判断是否有下一页
     *
     * @param currentPage 当前页码
     * @param totalPages  总页数
     * @return 是否有下一页
     */
    public static boolean hasNextPage(int currentPage, int totalPages) {
        return currentPage < totalPages;
    }

    /**
     * 从分页结果中计算总页数
     *
     * @param pageData 分页数据
     * @return 总页数
     */
    public static int calculateTotalPages(PageData<?> pageData) {
        if (pageData == null) {
            return 0;
        }
        return calculateTotalPages(pageData.getTotal(), pageData.getPageSize());
    }

    /**
     * 从分页结果中判断是否有上一页
     *
     * @param pageData 分页数据
     * @return 是否有上一页
     */
    public static boolean hasPrevPage(PageData<?> pageData) {
        if (pageData == null) {
            return false;
        }
        return hasPrevPage(pageData.getPageNum());
    }

    /**
     * 从分页结果中判断是否有下一页
     *
     * @param pageData 分页数据
     * @return 是否有下一页
     */
    public static boolean hasNextPage(PageData<?> pageData) {
        if (pageData == null) {
            return false;
        }
        int totalPages = calculateTotalPages(pageData);
        return hasNextPage(pageData.getPageNum(), totalPages);
    }

    /**
     * 获取最后一页页码
     *
     * @param total    总记录数
     * @param pageSize 每页大小
     * @return 最后一页页码（至少为 1）
     */
    public static int getLastPageNum(long total, int pageSize) {
        int totalPages = calculateTotalPages(total, pageSize);
        return totalPages > 0 ? totalPages : 1;
    }

    /**
     * 从分页结果中获取最后一页页码
     *
     * @param pageData 分页数据
     * @return 最后一页页码（至少为 1）
     */
    public static int getLastPageNum(PageData<?> pageData) {
        if (pageData == null) {
            return 1;
        }
        return getLastPageNum(pageData.getTotal(), pageData.getPageSize());
    }
}


