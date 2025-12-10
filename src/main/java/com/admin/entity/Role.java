package com.admin.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 1, max = 50, message = "角色名称长度必须在1-50个字符之间")
    private String name;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(min = 1, max = 50, message = "角色编码长度必须在1-50个字符之间")
    private String code;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "描述长度不能超过200个字符")
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

