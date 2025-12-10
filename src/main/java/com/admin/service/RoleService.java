package com.admin.service;

import com.admin.dto.PageRequest;
import com.admin.dto.RoleQueryDTO;
import com.admin.entity.Role;
import com.admin.util.PageResult;

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

    /**
     * 分页查询角色
     *
     * @param request 分页请求
     * @param query   查询条件
     * @return 分页结果
     */
    PageResult<Role> pageRoles(PageRequest request, RoleQueryDTO query);

    /**
     * 根据条件查询角色列表
     *
     * @param query 查询条件
     * @return 角色列表
     */
    List<Role> listRolesByCondition(RoleQueryDTO query);

    void saveRole(Role role);

    void updateRole(Role role);

    /**
     * 更新角色状态
     *
     * @param id        角色ID
     * @param isEnabled 是否启用
     */
    void updateRoleStatus(Long id, Boolean isEnabled);

    void deleteRole(Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     */
    void batchDeleteRoles(List<Long> ids);

    /**
     * 批量更新角色状态
     *
     * @param ids       角色ID列表
     * @param isEnabled 是否启用
     */
    void batchUpdateRoleStatus(List<Long> ids, Boolean isEnabled);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);

    /**
     * 分配角色权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 移除角色权限
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     */
    void removeRolePermission(Long roleId, Long permissionId);
}

