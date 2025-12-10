package com.admin.views.role;

import com.admin.component.BaseFormDialog;
import com.admin.entity.Role;
import com.admin.service.RoleService;
import com.admin.views.MainLayout;
import com.admin.views.base.BaseListView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * 角色列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "roles", layout = MainLayout.class)
@PageTitle("角色管理")
public class RoleListView extends BaseListView<Role, RoleService> {

    public RoleListView(RoleService roleService) {
        super(roleService, Role.class, "角色", "添加角色", "role-list-view");
    }

    @Override
    protected void configureColumns() {
        grid.addColumn(Role::getId).setHeader("ID").setWidth("80px");
        grid.addColumn(Role::getName).setHeader("角色名称").setAutoWidth(true);
        grid.addColumn(Role::getCode).setHeader("角色编码").setAutoWidth(true);
        grid.addColumn(Role::getDescription).setHeader("描述").setAutoWidth(true);
        grid.addColumn(role -> role.getIsEnabled() != null && role.getIsEnabled() ? "启用" : "禁用")
                .setHeader("状态")
                .setAutoWidth(true);
        grid.addColumn(Role::getCreatedAt).setHeader("创建时间").setAutoWidth(true);
        grid.addColumn(Role::getUpdatedAt).setHeader("更新时间").setAutoWidth(true);
    }

    @Override
    protected List<Role> getListData() {
        return service.listRoles();
    }

    @Override
    protected BaseFormDialog<Role> getFormDialog(boolean isEdit, Role entity) {
        RoleFormDialog dialog = new RoleFormDialog(service, isEdit, this::updateList);
        return dialog;
    }

    @Override
    protected void performDelete(Role entity) {
        service.deleteRole(entity.getId());
    }
}
