package com.admin.views.role;

import com.admin.entity.Role;
import com.admin.service.RoleService;
import com.admin.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * 角色列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "roles", layout = MainLayout.class)
@PageTitle("角色管理")
public class RoleListView extends VerticalLayout {

    private final RoleService roleService;
    private final Grid<Role> grid = new Grid<>(Role.class, false);

    public RoleListView(RoleService roleService) {
        this.roleService = roleService;
        addClassName("role-list-view");
        setSizeFull();

        configureGrid();
        add(getToolbar(), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addColumn(Role::getId).setHeader("ID").setWidth("80px");
        grid.addColumn(Role::getName).setHeader("角色名称");
        grid.addColumn(Role::getCode).setHeader("角色编码");
        grid.addColumn(Role::getDescription).setHeader("描述");
        grid.addColumn(role -> role.getIsEnabled() ? "启用" : "禁用").setHeader("状态");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("添加角色");
        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(roleService.listRoles());
    }
}

