package com.admin.views.user;

import com.admin.entity.Role;
import com.admin.entity.User;
import com.admin.service.RoleService;
import com.admin.service.UserService;
import com.admin.util.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色分配对话框
 *
 * @author Admin
 * @date 2024-01-01
 */
public class UserRoleAssignDialog extends Dialog {

    private final UserService userService;
    private final RoleService roleService;
    private final User user;
    private final Runnable refreshCallback;

    private VerticalLayout contentLayout;
    private List<Checkbox> roleCheckboxes = new ArrayList<>();
    private Button saveButton;
    private Button cancelButton;

    public UserRoleAssignDialog(UserService userService, RoleService roleService, User user, Runnable refreshCallback) {
        this.userService = userService;
        this.roleService = roleService;
        this.user = user;
        this.refreshCallback = refreshCallback;

        setHeaderTitle("分配角色 - " + user.getUserName());
        setWidth("500px");
        setHeight("600px");

        buildContent();
    }

    private void buildContent() {
        contentLayout = new VerticalLayout();
        contentLayout.setSpacing(true);
        contentLayout.setPadding(true);
        contentLayout.setWidthFull();

        // 获取所有角色
        List<Role> allRoles = roleService.listRoles();
        
        // 获取用户已分配的角色ID
        List<Long> userRoleIds = userService.getUserRoleIds(user.getId());

        // 创建角色复选框
        for (Role role : allRoles) {
            Checkbox checkbox = new Checkbox(role.getName() + " (" + role.getCode() + ")");
            checkbox.setValue(userRoleIds.contains(role.getId()));
            checkbox.setEnabled(role.getIsEnabled() != null && role.getIsEnabled());
            roleCheckboxes.add(checkbox);
            contentLayout.add(checkbox);
        }

        // 创建按钮
        saveButton = new Button("保存");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        cancelButton = new Button("取消");
        cancelButton.addClickListener(e -> close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.END);

        add(contentLayout, buttonLayout);
    }

    private void save() {
        try {
            // 获取选中的角色ID
            List<Long> selectedRoleIds = new ArrayList<>();
            List<Role> allRoles = roleService.listRoles();
            
            for (int i = 0; i < roleCheckboxes.size() && i < allRoles.size(); i++) {
                if (roleCheckboxes.get(i).getValue()) {
                    selectedRoleIds.add(allRoles.get(i).getId());
                }
            }

            // 保存用户角色关联
            userService.assignRoles(user.getId(), selectedRoleIds);

            NotificationUtil.showSuccess("分配角色成功");
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            close();
        } catch (Exception e) {
            NotificationUtil.showError("分配角色失败：" + e.getMessage());
        }
    }
}

