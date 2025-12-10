package com.admin.views.menu;

import com.admin.entity.Menu;
import com.admin.service.MenuService;
import com.admin.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * 菜单列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "menus", layout = MainLayout.class)
@PageTitle("菜单管理")
public class MenuListView extends VerticalLayout {

    private final MenuService menuService;
    private final Grid<Menu> grid = new Grid<>(Menu.class, false);

    public MenuListView(MenuService menuService) {
        this.menuService = menuService;
        addClassName("menu-list-view");
        setSizeFull();

        configureGrid();
        add(getToolbar(), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addColumn(Menu::getId).setHeader("ID").setWidth("80px");
        grid.addColumn(Menu::getName).setHeader("菜单名称");
        grid.addColumn(Menu::getPath).setHeader("路径");
        grid.addColumn(Menu::getComponent).setHeader("组件");
        grid.addColumn(Menu::getIcon).setHeader("图标");
        grid.addColumn(Menu::getSort).setHeader("排序");
        grid.addColumn(menu -> menu.getIsEnabled() ? "启用" : "禁用").setHeader("状态");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("添加菜单");
        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(menuService.listMenus());
    }
}

