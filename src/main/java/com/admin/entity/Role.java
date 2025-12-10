package com.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 是否删除
     */
    private Integer deleted;
}

