package com.admin.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * 所有实体类必须继承此类，包含通用字段：id、createdAt、updatedAt
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
public abstract class BaseEntity {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

