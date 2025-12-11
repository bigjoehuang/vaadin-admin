package com.admin.dto;

import lombok.Data;

/**
 * 用户查询条件DTO
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
public class UserQueryDTO {
    /**
     * 用户名（模糊查询）
     */
    private String userName;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 邮箱（模糊查询）
     */
    private String email;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 是否启用
     */
    private Boolean isEnabled;
}



