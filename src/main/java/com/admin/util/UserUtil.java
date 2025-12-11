package com.admin.util;

import com.admin.entity.User;
import com.admin.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 用户工具类
 * 用于获取当前登录用户信息
 *
 * @author Admin
 * @date 2024-01-01
 */
@Component
public class UserUtil {

    private static UserService userService;

    public UserUtil(UserService userService) {
        UserUtil.userService = userService;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，如果未登录返回null
     */
    public static String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && 
            authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前登录用户
     *
     * @return 用户对象，如果未登录返回null
     */
    public static User getCurrentUser() {
        String userName = getCurrentUserName();
        if (userName != null && userService != null) {
            return userService.getUserByUserName(userName);
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，如果未登录返回null
     */
    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}





