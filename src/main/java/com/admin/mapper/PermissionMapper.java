package com.admin.mapper;

import com.admin.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限 Mapper 接口
 *
 * @author Admin
 * @date 2024-01-01
 */
@Mapper
public interface PermissionMapper {
    Permission selectById(@Param("id") Long id);

    List<Permission> selectAll();

    List<Permission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限列表（通过用户角色关联）
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> selectByUserId(@Param("userId") Long userId);

    int insert(Permission permission);

    int updateById(Permission permission);

    int deleteById(@Param("id") Long id);
}

