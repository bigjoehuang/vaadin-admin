package com.admin.service.impl;

import com.admin.entity.Menu;
import com.admin.mapper.MenuMapper;
import com.admin.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜单服务实现
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public Menu getMenuById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public List<Menu> listMenus() {
        return menuMapper.selectAll();
    }

    @Override
    public List<Menu> getMenusByParentId(Long parentId) {
        return menuMapper.selectByParentId(parentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(Menu menu) {
        menuMapper.insert(menu);
        log.info("保存菜单成功，ID: {}", menu.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Menu menu) {
        menuMapper.updateById(menu);
        log.info("更新菜单成功，ID: {}", menu.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        menuMapper.deleteById(id);
        log.info("删除菜单成功，ID: {}", id);
    }
}

