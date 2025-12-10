package com.admin.service;

import com.admin.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author Admin
 * @date 2024-01-01
 */
public interface PermissionService {
    Permission getPermissionById(Long id);

    List<Permission> listPermissions();

    List<Permission> getPermissionsByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限列表（通过用户角色关联）
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);

    void savePermission(Permission permission);

    void updatePermission(Permission permission);

    void deletePermission(Long id);
}

