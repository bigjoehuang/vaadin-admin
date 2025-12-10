package com.admin.service;

import com.admin.entity.Menu;

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

    void saveMenu(Menu menu);

    void updateMenu(Menu menu);

    void deleteMenu(Long id);
}

