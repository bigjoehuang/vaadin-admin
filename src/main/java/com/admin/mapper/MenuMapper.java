package com.admin.mapper;

import com.admin.dto.MenuQueryDTO;
import com.admin.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单 Mapper 接口
 *
 * @author Admin
 * @date 2024-01-01
 */
@Mapper
public interface MenuMapper {
    Menu selectById(@Param("id") Long id);

    List<Menu> selectAll();

    List<Menu> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据条件查询菜单列表
     *
     * @param query 查询条件
     * @return 菜单列表
     */
    List<Menu> selectByCondition(MenuQueryDTO query);

    /**
     * 根据条件统计菜单数量
     *
     * @param query 查询条件
     * @return 记录数
     */
    Long countByCondition(MenuQueryDTO query);

    /**
     * 统计子菜单数量
     *
     * @param parentId 父菜单ID
     * @return 子菜单数量
     */
    int countChildren(@Param("parentId") Long parentId);

    int insert(Menu menu);

    int updateById(Menu menu);

    /**
     * 更新菜单状态
     *
     * @param id        菜单ID
     * @param isEnabled 是否启用
     * @return 更新行数
     */
    int updateStatusById(@Param("id") Long id, @Param("isEnabled") Boolean isEnabled);

    int deleteById(@Param("id") Long id);

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID列表
     * @return 删除行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 批量更新菜单状态
     *
     * @param ids       菜单ID列表
     * @param isEnabled 是否启用
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isEnabled") Boolean isEnabled);
}

