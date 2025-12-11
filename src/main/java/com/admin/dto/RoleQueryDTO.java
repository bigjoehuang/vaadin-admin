package com.admin.dto;

import lombok.Data;

/**
 * 角色查询条件DTO
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
public class RoleQueryDTO {
    /**
     * 角色名称（模糊查询）
     */
    private String name;

    /**
     * 角色编码（模糊查询）
     */
    private String code;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}






