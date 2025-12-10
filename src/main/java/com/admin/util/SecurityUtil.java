package com.admin.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全工具类
 *
 * @author Admin
 * @date 2024-01-01
 */
public class SecurityUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     */
    public static String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

