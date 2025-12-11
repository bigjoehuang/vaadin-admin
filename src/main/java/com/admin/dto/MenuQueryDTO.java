package com.admin.dto;

import lombok.Data;

/**
 * 菜单查询条件DTO
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
public class MenuQueryDTO {
    /**
     * 菜单名称（模糊查询）
     */
    private String name;

    /**
     * 菜单路径（模糊查询）
     */
    private String path;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}






