package com.admin.util;

import com.admin.exception.BusinessException;
import com.admin.exception.ErrorCode;

import java.util.regex.Pattern;

/**
 * 密码验证工具类
 *
 * @author Admin
 * @date 2024-01-01
 */
public class PasswordValidator {

    // 密码复杂度规则：至少8个字符，包含大小写字母、数字和特殊字符
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    /**
     * 验证密码复杂度
     *
     * @param password 密码
     * @return 是否符合要求
     */
    public static boolean validate(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return pattern.matcher(password).matches();
    }

    /**
     * 验证密码复杂度并抛出异常
     *
     * @param password 密码
     */
    public static void validateAndThrow(String password) {
        if (!validate(password)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码必须至少8个字符，包含大小写字母、数字和特殊字符");
        }
    }

    /**
     * 获取密码复杂度规则描述
     *
     * @return 规则描述
     */
    public static String getRuleDescription() {
        return "密码必须至少8个字符，包含大小写字母、数字和特殊字符";
    }
}
