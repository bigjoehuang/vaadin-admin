package com.admin.service.impl;

import com.admin.entity.Permission;
import com.admin.mapper.PermissionMapper;
import com.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 权限服务实现
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public Permission getPermissionById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public List<Permission> listPermissions() {
        return permissionMapper.selectAll();
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return permissionMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePermission(Permission permission) {
        permissionMapper.insert(permission);
        log.info("保存权限成功，ID: {}", permission.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(Permission permission) {
        permissionMapper.updateById(permission);
        log.info("更新权限成功，ID: {}", permission.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        permissionMapper.deleteById(id);
        log.info("删除权限成功，ID: {}", id);
    }
}

