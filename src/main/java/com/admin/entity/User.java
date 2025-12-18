package com.admin.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^", message = "手机号格式不正确")
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
    
    /**
     * 登录失败次数
     */
    private Integer loginFailCount;
    
    /**
     * 最后登录失败时间
     */
    private Long lastLoginFailTime;
    
    /**
     * 锁定状态
     */
    private Boolean isLocked;
}










