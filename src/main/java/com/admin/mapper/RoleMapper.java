package com.admin.mapper;

import com.admin.dto.RoleQueryDTO;
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

    Role selectByCode(@Param("code") String code);

    /**
     * 根据编码查询角色（排除指定ID，用于编辑时验证唯一性）
     *
     * @param code 角色编码
     * @param excludeId 排除的ID
     * @return 角色信息
     */
    Role selectByCodeExcludeId(@Param("code") String code, @Param("excludeId") Long excludeId);

    List<Role> selectAll();

    /**
     * 根据条件查询角色列表
     *
     * @param query 查询条件
     * @return 角色列表
     */
    List<Role> selectByCondition(RoleQueryDTO query);

    /**
     * 根据条件统计角色数量
     *
     * @param query 查询条件
     * @return 记录数
     */
    Long countByCondition(RoleQueryDTO query);

    int insert(Role role);

    int updateById(Role role);

    /**
     * 更新角色状态
     *
     * @param id 角色ID
     * @param isEnabled 是否启用
     * @return 更新行数
     */
    int updateStatusById(@Param("id") Long id, @Param("isEnabled") Boolean isEnabled);

    int deleteById(@Param("id") Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     * @return 删除行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 批量更新角色状态
     *
     * @param ids 角色ID列表
     * @param isEnabled 是否启用
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isEnabled") Boolean isEnabled);
}

