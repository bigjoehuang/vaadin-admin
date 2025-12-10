package com.admin.util;

import com.admin.entity.User;
import com.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 *
 * @author Admin
 * @date 2024-01-01
 */
@Component
public class SecurityUtil {

    private static final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = 
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        SecurityUtil.userService = userService;
    }

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

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return principal.toString();
        }
        return null;
    }

    /**
     * 获取当前登录用户
     */
    public static User getCurrentUser() {
        String username = getCurrentUsername();
        if (username != null && userService != null) {
            return userService.getUserByUserName(username);
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}

