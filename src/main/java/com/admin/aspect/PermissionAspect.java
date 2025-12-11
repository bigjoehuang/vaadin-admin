package com.admin.aspect;

import com.admin.annotation.RequiresPermission;
import com.admin.exception.BusinessException;
import com.admin.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限检查切面
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Pointcut("@annotation(com.admin.annotation.RequiresPermission)")
    public void permissionPointcut() {
    }

    @Before("permissionPointcut()")
    public void checkPermission(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);

        if (requiresPermission == null) {
            // 检查类级别的注解
            requiresPermission = method.getDeclaringClass().getAnnotation(RequiresPermission.class);
        }

        if (requiresPermission == null) {
            return;
        }

        // 获取当前用户的权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> authoritySet = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // 检查权限
        String[] permissions = requiresPermission.value();
        String[] roles = requiresPermission.roles();
        boolean requireAll = requiresPermission.requireAll();

        // 检查角色权限
        if (roles.length > 0) {
            boolean hasRole = false;
            for (String role : roles) {
                String roleAuthority = "ROLE_" + role;
                if (authoritySet.contains(roleAuthority) || authoritySet.contains(role)) {
                    hasRole = true;
                    if (!requireAll) {
                        break;
                    }
                } else if (requireAll) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "缺少角色权限: " + role);
                }
            }
            if (!hasRole && !requireAll) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "缺少必要的角色权限");
            }
        }

        // 检查功能权限
        if (permissions.length > 0) {
            boolean hasPermission = false;
            for (String permission : permissions) {
                if (authoritySet.contains(permission)) {
                    hasPermission = true;
                    if (!requireAll) {
                        break;
                    }
                } else if (requireAll) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "缺少权限: " + permission);
                }
            }
            if (!hasPermission && !requireAll) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "缺少必要的功能权限");
            }
        }
    }
}



