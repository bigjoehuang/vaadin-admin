package com.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author Admin
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 服务器错误
     */
    SERVER_ERROR(500, "服务器错误"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1001, "用户不存在"),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(1002, "用户名或密码错误"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(1003, "用户已存在"),

    /**
     * 角色不存在
     */
    ROLE_NOT_FOUND(2001, "角色不存在"),

    /**
     * 权限不存在
     */
    PERMISSION_NOT_FOUND(3001, "权限不存在");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;
}

