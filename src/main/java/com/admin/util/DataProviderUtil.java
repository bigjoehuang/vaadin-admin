package com.admin.util;

import com.admin.dto.PageRequest;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortOrder;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * DataProvider 工具类
 * 用于将 Service 层的分页查询转换为 Vaadin 的 DataProvider，实现 Grid 懒加载
 *
 * @author Admin
 * @date 2024-01-01
 */
public class DataProviderUtil {

    /**
     * 创建分页 DataProvider（使用自定义分页请求）
     *
     * @param querySupplier 查询条件提供者（用于获取当前查询条件）
     * @param pageRequestSupplier 分页请求提供者（用于获取当前分页请求）
     * @param pageFunction 分页查询函数，接收 PageRequest 和查询条件，返回 PageResult
     * @param <T> 实体类型
     * @param <Q> 查询条件类型
     * @return DataProvider
     */
    public static <T, Q> AbstractBackEndDataProvider<T, Void> createPageDataProvider(
            Supplier<Q> querySupplier,
            Supplier<PageRequest> pageRequestSupplier,
            BiFunction<PageRequest, Q, PageResult<T>> pageFunction) {
        return new AbstractBackEndDataProvider<T, Void>() {
            @Override
            protected Stream<T> fetchFromBackEnd(Query<T, Void> query) {
                // 获取当前分页请求
                PageRequest pageRequest = pageRequestSupplier.get();
                if (pageRequest == null) {
                    pageRequest = new PageRequest();
                }
                
                // 创建一个新的 PageRequest 副本，避免修改原始对象
                PageRequest request = new PageRequest();
                request.setPageNum(pageRequest.getPageNum());
                request.setPageSize(pageRequest.getPageSize());
                request.setSortField(pageRequest.getSortField());
                request.setSortOrder(pageRequest.getSortOrder());
                
                // 处理排序（优先使用 Grid 的排序，如果没有则使用分页请求的排序）
                if (query.getSortOrders() != null && !query.getSortOrders().isEmpty()) {
                    SortOrder<?> sortOrder = query.getSortOrders().get(0);
                    String sortField = getSortField(sortOrder);
                    if (sortField != null) {
                        request.setSortField(sortField);
                        request.setSortOrder(sortOrder.getDirection() == com.vaadin.flow.data.provider.SortDirection.ASCENDING ? "ASC" : "DESC");
                    }
                }
                
                // 获取查询条件
                Q queryDTO = querySupplier.get();
                
                // 执行分页查询
                PageResult<T> pageResult = pageFunction.apply(request, queryDTO);
                
                if (pageResult != null && pageResult.getData() != null) {
                    List<T> list = pageResult.getData().getList();
                    return list != null ? list.stream() : Stream.empty();
                }
                
                return Stream.empty();
            }

            @Override
            protected int sizeInBackEnd(Query<T, Void> query) {
                // 获取当前分页请求
                PageRequest pageRequest = pageRequestSupplier.get();
                if (pageRequest == null) {
                    pageRequest = new PageRequest();
                }
                
                // 使用第一页获取总数
                PageRequest countRequest = new PageRequest();
                countRequest.setPageNum(1);
                countRequest.setPageSize(1);
                
                // 获取查询条件
                Q queryDTO = querySupplier.get();
                
                // 执行分页查询获取总数
                PageResult<T> pageResult = pageFunction.apply(countRequest, queryDTO);
                
                if (pageResult != null && pageResult.getData() != null) {
                    Long total = pageResult.getData().getTotal();
                    return total != null ? total.intValue() : 0;
                }
                
                return 0;
            }
            
            /**
             * 从 SortOrder 中提取排序字段名
             * 支持通过 getSorted() 方法获取字段名，或通过反射获取
             */
            private String getSortField(SortOrder<?> sortOrder) {
                try {
                    // 尝试通过 getSorted() 方法获取
                    Object sorted = sortOrder.getSorted();
                    if (sorted != null) {
                        // 如果是方法引用，尝试获取方法名
                        String sortedStr = sorted.toString();
                        // 处理 Lambda 表达式和方法引用
                        // 例如: "User::getId" -> "id"
                        if (sortedStr.contains("::")) {
                            String[] parts = sortedStr.split("::");
                            if (parts.length == 2) {
                                String methodName = parts[1].trim();
                                // 移除 get 前缀并转换为小写
                                if (methodName.startsWith("get")) {
                                    String fieldName = methodName.substring(3);
                                    // 首字母小写
                                    if (fieldName.length() > 0) {
                                        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // 忽略异常，使用默认处理
                }
                return null;
            }
        };
    }
}

