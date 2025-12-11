package com.admin.service.base;

import java.util.List;

/**
 * 基础服务接口
 *
 * @param <T> 实体类型
 * @author Admin
 * @date 2024-01-01
 */
public interface BaseService<T> {
    /**
     * 根据ID查询
     */
    T getById(Long id);

    /**
     * 查询所有
     */
    List<T> listAll();

    /**
     * 保存
     */
    void save(T entity);

    /**
     * 更新
     */
    void updateById(T entity);

    /**
     * 删除
     */
    void deleteById(Long id);
}


