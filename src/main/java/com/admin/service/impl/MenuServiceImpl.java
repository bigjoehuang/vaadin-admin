package com.admin.service.impl;

import com.admin.dto.MenuQueryDTO;
import com.admin.dto.PageRequest;
import com.admin.entity.Menu;
import com.admin.exception.BusinessException;
import com.admin.exception.ErrorCode;
import com.admin.mapper.MenuMapper;
import com.admin.service.MenuService;
import com.admin.util.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        return menu;
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
    public PageResult<Menu> pageMenus(PageRequest request, MenuQueryDTO query) {
        // 参数校验
        if (request == null) {
            request = new PageRequest(); // 使用默认分页参数
        }
        if (query == null) {
            query = new MenuQueryDTO(); // 使用默认查询条件
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<Menu> menus = menuMapper.selectByCondition(query);
        PageInfo<Menu> pageInfo = new PageInfo<>(menus);

        return PageResult.success(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize()
        );
    }

    @Override
    public List<Menu> listMenusByCondition(MenuQueryDTO query) {
        return menuMapper.selectByCondition(query);
    }

    @Override
    public List<Menu> getMenuTree() {
        // 获取所有菜单
        List<Menu> allMenus = menuMapper.selectAll();
        
        // 构建菜单树
        return buildMenuTree(allMenus, null);
    }

    /**
     * 递归构建菜单树
     *
     * @param allMenus 所有菜单列表
     * @param parentId 父菜单ID，null表示根菜单
     * @return 菜单树列表
     */
    private List<Menu> buildMenuTree(List<Menu> allMenus, Long parentId) {
        List<Menu> tree = new ArrayList<>();
        
        for (Menu menu : allMenus) {
            // 判断是否为当前父菜单的子菜单
            Long menuParentId = menu.getParentId();
            if ((parentId == null && menuParentId == null) || 
                (parentId != null && parentId.equals(menuParentId))) {
                // 递归获取子菜单（虽然Menu实体没有children字段，但可以用于后续扩展）
                buildMenuTree(allMenus, menu.getId());
                tree.add(menu);
            }
        }
        
        // 按sort排序
        tree.sort((m1, m2) -> {
            Integer sort1 = m1.getSort() != null ? m1.getSort() : 0;
            Integer sort2 = m2.getSort() != null ? m2.getSort() : 0;
            return sort1.compareTo(sort2);
        });
        
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(Menu menu) {
        // 如果设置了父菜单，验证父菜单是否存在
        if (menu.getParentId() != null) {
            Menu parentMenu = menuMapper.selectById(menu.getParentId());
            if (parentMenu == null) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "父菜单不存在");
            }
        }
        
        // 设置默认值
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getIsEnabled() == null) {
            menu.setIsEnabled(true);
        }
        
        menuMapper.insert(menu);
        log.info("保存菜单成功，ID: {}, 名称: {}", menu.getId(), menu.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Menu menu) {
        Menu existMenu = menuMapper.selectById(menu.getId());
        if (existMenu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        
        // 如果设置了父菜单，验证父菜单是否存在且不是自己
        if (menu.getParentId() != null) {
            if (menu.getParentId().equals(menu.getId())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "不能将自己设置为父菜单");
            }
            Menu parentMenu = menuMapper.selectById(menu.getParentId());
            if (parentMenu == null) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "父菜单不存在");
            }
        }
        
        menuMapper.updateById(menu);
        log.info("更新菜单成功，ID: {}, 名称: {}", menu.getId(), menu.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuStatus(Long id, Boolean isEnabled) {
        Menu existMenu = menuMapper.selectById(id);
        if (existMenu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        menuMapper.updateStatusById(id, isEnabled);
        log.info("更新菜单状态成功，ID: {}, 状态: {}", id, isEnabled ? "启用" : "禁用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        Menu existMenu = menuMapper.selectById(id);
        if (existMenu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        
        // 检查是否有子菜单
        if (hasChildren(id)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "该菜单下有子菜单，无法删除");
        }
        
        menuMapper.deleteById(id);
        log.info("删除菜单成功，ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteMenus(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "菜单ID列表不能为空");
        }
        
        // 检查是否有子菜单
        for (Long id : ids) {
            if (hasChildren(id)) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "菜单ID " + id + " 下有子菜单，无法删除");
            }
        }
        
        int count = menuMapper.batchDeleteByIds(ids);
        log.info("批量删除菜单成功，删除数量: {}", count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateMenuStatus(List<Long> ids, Boolean isEnabled) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "菜单ID列表不能为空");
        }
        int count = menuMapper.batchUpdateStatus(ids, isEnabled);
        log.info("批量更新菜单状态成功，更新数量: {}, 状态: {}", count, isEnabled ? "启用" : "禁用");
    }

    @Override
    public boolean hasChildren(Long id) {
        int count = menuMapper.countChildren(id);
        return count > 0;
    }
}

