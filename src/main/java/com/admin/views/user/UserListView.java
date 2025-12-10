package com.admin.views.user;

import com.admin.entity.User;
import com.admin.service.RoleService;
import com.admin.service.UserService;
import com.admin.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * 用户列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "users", layout = MainLayout.class)
@PageTitle("用户管理")
public class UserListView extends VerticalLayout {

    private final UserService userService;
    private final RoleService roleService;
    private final Grid<User> grid = new Grid<>(User.class, false);

    public UserListView(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        addClassName("user-list-view");
        setSizeFull();

        configureGrid();
        add(getToolbar(), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addColumn(User::getId).setHeader("ID").setWidth("80px");
        grid.addColumn(User::getUserName).setHeader("用户名");
        grid.addColumn(User::getNickname).setHeader("昵称");
        grid.addColumn(User::getEmail).setHeader("邮箱");
        grid.addColumn(User::getPhone).setHeader("手机号");
        grid.addColumn(user -> user.getIsEnabled() ? "启用" : "禁用").setHeader("状态");
        
        // 添加操作列
        grid.addComponentColumn(user -> {
            HorizontalLayout actionLayout = new HorizontalLayout();
            actionLayout.setSpacing(true);

            Button assignRoleButton = new Button("分配角色", new Icon(VaadinIcon.USER_CHECK));
            assignRoleButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            assignRoleButton.addClickListener(e -> {
                UserRoleAssignDialog dialog = new UserRoleAssignDialog(userService, roleService, user, this::updateList);
                dialog.open();
            });

            actionLayout.add(assignRoleButton);
            return actionLayout;
        }).setHeader("操作").setWidth("150px").setFlexGrow(0);
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("添加用户");
        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(userService.listUsers());
    }
}

