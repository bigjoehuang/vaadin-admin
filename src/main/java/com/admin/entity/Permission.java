package com.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {
    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限类型：menu-菜单，button-按钮，api-接口
     */
    private String type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 权限路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 是否删除
     */
    private Integer deleted;
}


