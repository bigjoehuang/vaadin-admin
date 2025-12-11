package com.admin.exception;

import com.admin.util.I18NUtil;
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
    SUCCESS(200, "error.operation.success"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "error.param.error"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "error.unauthorized"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "error.forbidden"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "error.not.found"),

    /**
     * 服务器错误
     */
    SERVER_ERROR(500, "error.server.error"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1001, "error.user.not.found"),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(1002, "error.username.or.password.error"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(1003, "error.user.already.exists"),

    /**
     * 角色不存在
     */
    ROLE_NOT_FOUND(2001, "error.role.not.found"),

    /**
     * 角色已存在
     */
    ROLE_ALREADY_EXISTS(2002, "error.role.already.exists"),

    /**
     * 权限不存在
     */
    PERMISSION_NOT_FOUND(3001, "error.permission.not.found"),

    /**
     * 菜单不存在
     */
    MENU_NOT_FOUND(4001, "error.menu.not.found");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息 key（国际化）
     */
    private final String messageKey;

    /**
     * 获取错误信息 key
     *
     * @return 错误信息 key
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * 获取国际化错误信息
     *
     * @return 错误信息
     */
    public String getMessage() {
        return I18NUtil.get(messageKey);
    }
}

