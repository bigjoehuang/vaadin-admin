package com.admin.views.role;

import com.admin.entity.Permission;
import com.admin.entity.Role;
import com.admin.service.PermissionService;
import com.admin.service.RoleService;
import com.admin.util.I18NUtil;
import com.admin.util.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色权限分配对话框
 *
 * @author Admin
 * @date 2024-01-01
 */
public class RolePermissionAssignDialog extends Dialog {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final Role role;
    private final Runnable refreshCallback;

    private VerticalLayout contentLayout;
    private List<Checkbox> permissionCheckboxes = new ArrayList<>();
    private Button saveButton;
    private Button cancelButton;

    public RolePermissionAssignDialog(RoleService roleService, PermissionService permissionService, Role role, Runnable refreshCallback) {
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.role = role;
        this.refreshCallback = refreshCallback;

        setHeaderTitle(I18NUtil.get("role.assign.permission.title", role.getName()));
        setWidth("600px");
        setHeight("700px");

        buildContent();
    }

    private void buildContent() {
        contentLayout = new VerticalLayout();
        contentLayout.setSpacing(true);
        contentLayout.setPadding(true);
        contentLayout.setWidthFull();

        // 获取所有权限
        List<Permission> allPermissions = permissionService.listPermissions();
        
        // 获取角色已分配的权限ID
        List<Long> rolePermissionIds = roleService.getRolePermissionIds(role.getId());

        // 按类型分组权限
        Map<String, List<Permission>> permissionsByType = allPermissions.stream()
                .collect(Collectors.groupingBy(p -> p.getType() != null ? p.getType() : I18NUtil.get("common.other")));

        // 创建权限复选框，按类型分组显示
        for (Map.Entry<String, List<Permission>> entry : permissionsByType.entrySet()) {
            String type = entry.getKey();
            List<Permission> permissions = entry.getValue();
            
            // 添加类型标题
            com.vaadin.flow.component.html.H3 typeHeader = new com.vaadin.flow.component.html.H3(type);
            contentLayout.add(typeHeader);
            
            // 添加该类型下的权限复选框
            for (Permission permission : permissions) {
                Checkbox checkbox = new Checkbox(permission.getName() + " (" + permission.getCode() + ")");
                checkbox.setValue(rolePermissionIds.contains(permission.getId()));
                checkbox.setEnabled(permission.getIsEnabled() != null && permission.getIsEnabled());
                permissionCheckboxes.add(checkbox);
                contentLayout.add(checkbox);
            }
        }

        // 创建按钮
        saveButton = new Button(I18NUtil.get("common.save"));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        cancelButton = new Button(I18NUtil.get("common.cancel"));
        cancelButton.addClickListener(e -> close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.END);

        add(contentLayout, buttonLayout);
    }

    private void save() {
        try {
            // 获取选中的权限ID
            List<Long> selectedPermissionIds = new ArrayList<>();
            List<Permission> allPermissions = permissionService.listPermissions();
            
            for (int i = 0; i < permissionCheckboxes.size() && i < allPermissions.size(); i++) {
                if (permissionCheckboxes.get(i).getValue()) {
                    selectedPermissionIds.add(allPermissions.get(i).getId());
                }
            }

            // 保存角色权限关联
            roleService.assignPermissions(role.getId(), selectedPermissionIds);

            NotificationUtil.showSuccess(I18NUtil.get("role.assign.permission.success"));
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            close();
        } catch (Exception e) {
            NotificationUtil.showError(I18NUtil.get("role.assign.permission.failed", e.getMessage()));
        }
    }
}

