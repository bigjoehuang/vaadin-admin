package com.admin.mapper;

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

    int insert(Menu menu);

    int updateById(Menu menu);

    int deleteById(@Param("id") Long id);
}

