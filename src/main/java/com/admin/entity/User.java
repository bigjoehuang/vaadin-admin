package com.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 是否删除
     */
    private Integer deleted;
}


