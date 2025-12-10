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

    void savePermission(Permission permission);

    void updatePermission(Permission permission);

    void deletePermission(Long id);
}

