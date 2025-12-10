package com.admin.mapper;

import com.admin.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper 接口
 *
 * @author Admin
 * @date 2024-01-01
 */
@Mapper
public interface RoleMapper {
    Role selectById(@Param("id") Long id);

    List<Role> selectAll();

    int insert(Role role);

    int updateById(Role role);

    int deleteById(@Param("id") Long id);
}

