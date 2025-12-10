package com.admin.views;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 需要认证的路由守卫
 * 所有需要认证的视图应该实现此接口
 *
 * @author Admin
 * @date 2024-01-01
 */
public interface AuthenticatedRoute extends BeforeEnterObserver {

    @Override
    default void beforeEnter(BeforeEnterEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 如果用户未认证或认证信息无效，重定向到登录页面
        if (authentication == null || 
            !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            event.forwardTo(LoginView.class);
        }
    }
}

