package com.admin.service;

import com.admin.dto.MenuQueryDTO;
import com.admin.dto.PageRequest;
import com.admin.entity.Menu;
import com.admin.util.PageResult;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author Admin
 * @date 2024-01-01
 */
public interface MenuService {
    Menu getMenuById(Long id);

    List<Menu> listMenus();

    List<Menu> getMenusByParentId(Long parentId);

    /**
     * 分页查询菜单
     *
     * @param request 分页请求
     * @param query  查询条件
     * @return 分页结果
     */
    PageResult<Menu> pageMenus(PageRequest request, MenuQueryDTO query);

    /**
     * 根据条件查询菜单列表
     *
     * @param query 查询条件
     * @return 菜单列表
     */
    List<Menu> listMenusByCondition(MenuQueryDTO query);

    /**
     * 获取菜单树（树形结构）
     *
     * @return 菜单树列表
     */
    List<Menu> getMenuTree();

    /**
     * 根据用户ID获取菜单树（考虑权限）
     *
     * @param userId 用户ID
     * @return 菜单树列表
     */
    List<Menu> getMenuTreeByUserId(Long userId);

    void saveMenu(Menu menu);

    void updateMenu(Menu menu);

    /**
     * 更新菜单状态
     *
     * @param id        菜单ID
     * @param isEnabled 是否启用
     */
    void updateMenuStatus(Long id, Boolean isEnabled);

    void deleteMenu(Long id);

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID列表
     */
    void batchDeleteMenus(List<Long> ids);

    /**
     * 批量更新菜单状态
     *
     * @param ids       菜单ID列表
     * @param isEnabled 是否启用
     */
    void batchUpdateMenuStatus(List<Long> ids, Boolean isEnabled);

    /**
     * 检查菜单是否有子菜单
     *
     * @param id 菜单ID
     * @return 是否有子菜单
     */
    boolean hasChildren(Long id);
}

