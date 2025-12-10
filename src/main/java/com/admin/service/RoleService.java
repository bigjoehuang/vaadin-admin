package com.admin.service;

import com.admin.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author Admin
 * @date 2024-01-01
 */
public interface RoleService {
    Role getRoleById(Long id);

    Role getRoleByCode(String code);

    List<Role> listRoles();

    void saveRole(Role role);

    void updateRole(Role role);

    void deleteRole(Long id);
}

