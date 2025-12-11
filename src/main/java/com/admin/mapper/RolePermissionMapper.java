package com.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联 Mapper 接口
 *
 * @author Admin
 * @date 2024-01-01
 */
@Mapper
public interface RolePermissionMapper {
    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID查询角色ID列表
     *
     * @param permissionId 权限ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 根据用户ID查询权限ID列表（通过用户角色关联）
     *
     * @param userId 用户ID
     * @return 权限ID列表
     */
    List<Long> selectPermissionIdsByUserId(@Param("userId") Long userId);

    /**
     * 批量插入角色权限关联
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 插入行数
     */
    int insertBatch(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 删除角色的所有权限关联
     *
     * @param roleId 角色ID
     * @return 删除行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除权限的所有角色关联
     *
     * @param permissionId 权限ID
     * @return 删除行数
     */
    int deleteByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 删除指定的角色权限关联
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     * @return 删除行数
     */
    int deleteByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}





