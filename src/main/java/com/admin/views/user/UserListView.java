package com.admin.views.user;

import com.admin.entity.User;
import com.admin.service.UserService;
import com.admin.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
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
    private final Grid<User> grid = new Grid<>(User.class, false);

    public UserListView(UserService userService) {
        this.userService = userService;
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

