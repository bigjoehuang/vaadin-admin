package com.admin.views.role;

import com.admin.entity.Role;
import com.admin.service.RoleService;
import com.admin.util.NotificationUtil;
import com.admin.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
        grid.addColumn(Role::getName).setHeader("角色名称").setAutoWidth(true);
        grid.addColumn(Role::getCode).setHeader("角色编码").setAutoWidth(true);
        grid.addColumn(Role::getDescription).setHeader("描述").setAutoWidth(true);
        grid.addColumn(role -> role.getIsEnabled() != null && role.getIsEnabled() ? "启用" : "禁用")
                .setHeader("状态")
                .setAutoWidth(true);
        grid.addColumn(Role::getCreatedAt).setHeader("创建时间").setAutoWidth(true);
        grid.addColumn(Role::getUpdatedAt).setHeader("更新时间").setAutoWidth(true);

        // 添加操作列
        grid.addComponentColumn(role -> {
            HorizontalLayout actionLayout = new HorizontalLayout();
            actionLayout.setSpacing(true);

            Button editButton = new Button("编辑", new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> editRole(role));

            Button deleteButton = new Button("删除", new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> deleteRole(role));

            actionLayout.add(editButton, deleteButton);
            return actionLayout;
        }).setHeader("操作").setWidth("180px").setFlexGrow(0);

        // 双击行编辑
        grid.addItemDoubleClickListener(e -> {
            if (e.getItem() != null) {
                editRole(e.getItem());
            }
        });

        grid.setSelectionMode(Grid.SelectionMode.NONE);
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("添加角色");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> addRole());

        Button refreshButton = new Button("刷新");
        refreshButton.addClickListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(addButton, refreshButton);
        toolbar.addClassName("toolbar");
        toolbar.setSpacing(true);
        return toolbar;
    }

    private void updateList() {
        try {
            grid.setItems(roleService.listRoles());
        } catch (Exception e) {
            NotificationUtil.showError("加载角色列表失败：" + e.getMessage());
        }
    }

    private void addRole() {
        RoleFormDialog dialog = new RoleFormDialog(roleService, false, this::updateList);
        dialog.open();
    }

    private void editRole(Role role) {
        RoleFormDialog dialog = new RoleFormDialog(roleService, true, this::updateList);
        dialog.setEntity(role);
        dialog.open();
    }

    private void deleteRole(Role role) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("确认删除");
        confirmDialog.setText("确定要删除角色 \"" + role.getName() + "\" 吗？此操作不可恢复。");
        confirmDialog.setConfirmText("删除");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.setCancelText("取消");

        confirmDialog.addConfirmListener(e -> {
            try {
                roleService.deleteRole(role.getId());
                NotificationUtil.showSuccess("删除角色成功");
                updateList();
            } catch (Exception ex) {
                NotificationUtil.showError("删除角色失败：" + ex.getMessage());
            }
        });

        confirmDialog.open();
    }
}
