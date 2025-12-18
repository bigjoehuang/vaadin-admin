package com.admin.config;

import com.admin.entity.Permission;
import com.admin.entity.User;
import com.admin.mapper.UserMapper;
import com.admin.mapper.UserRoleMapper;
import com.admin.service.PermissionService;
import com.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Spring Security 用户详情服务
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final PermissionService permissionService;
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUserName(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getDeleted() != null && user.getDeleted() == 1) {
            throw new UsernameNotFoundException("用户已被删除: " + username);
        }

        if (user.getIsEnabled() == null || !user.getIsEnabled()) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        if (user.getIsLocked() != null && user.getIsLocked()) {
            throw new UsernameNotFoundException("用户已被锁定: " + username);
        }

        // 从数据库查询用户的角色和权限
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // 查询用户的角色ID列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
        
        // 为每个角色添加 ROLE_ 前缀的权限
        for (Long roleId : roleIds) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleId));
        }
        
        // 查询用户的权限列表
        List<Permission> permissions = permissionService.getPermissionsByUserId(user.getId());
        
        // 为每个权限添加权限编码
        for (Permission permission : permissions) {
            if (permission.getCode() != null && !permission.getCode().isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(permission.getCode()));
            }
        }
        
        // 如果没有角色和权限，至少添加一个默认角色
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsEnabled())
                .build();
    }

    /**
     * 处理认证成功事件
     */
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        try {
            User user = userService.getUserByUserName(username);
            if (user != null) {
                // 重置登录失败次数
                userMapper.resetLoginFailCount(user.getId());
                log.info("用户登录成功，重置失败次数: {}", username);
            }
        } catch (Exception e) {
            log.error("处理登录成功事件失败: {}", e.getMessage());
        }
    }

    /**
     * 处理认证失败事件
     */
    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        try {
            User user = userService.getUserByUserName(username);
            if (user != null) {
                // 增加登录失败次数
                Integer failCount = user.getLoginFailCount() != null ? user.getLoginFailCount() : 0;
                failCount++;
                
                // 更新失败次数和时间
                userMapper.updateLoginFailCount(user.getId(), failCount);
                userMapper.updateLastLoginFailTime(user.getId(), System.currentTimeMillis());
                
                // 如果失败次数超过5次，锁定用户
                if (failCount >= 5) {
                    userMapper.updateLockStatus(user.getId(), true);
                    log.info("用户登录失败次数超过限制，已锁定: {}", username);
                } else {
                    log.info("用户登录失败，当前失败次数: {}，用户名: {}", failCount, username);
                }
            }
        } catch (Exception e) {
            log.error("处理登录失败事件失败: {}", e.getMessage());
        }
    }
}

