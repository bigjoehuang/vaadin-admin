package com.admin.config;

import com.admin.entity.Permission;
import com.admin.entity.User;
import com.admin.mapper.UserRoleMapper;
import com.admin.service.PermissionService;
import com.admin.service.UserService;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final PermissionService permissionService;
    private final UserRoleMapper userRoleMapper;

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
}

