package com.admin.views;

import com.admin.service.UserService;
import com.admin.util.NotificationUtil;
import com.admin.util.UserUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;

/**
 * 修改密码对话框
 *
 * @author Admin
 * @date 2024-01-01
 */
public class ChangePasswordDialog extends Dialog {

    private final UserService userService;
    private final PasswordField oldPasswordField;
    private final PasswordField newPasswordField;
    private final PasswordField confirmPasswordField;
    private final Button saveButton;
    private final Button cancelButton;
    private final Binder<PasswordData> binder;

    /**
     * 密码数据类（用于Binder验证）
     */
    private static class PasswordData {
        private String oldPassword;
        private String newPassword;
        private String confirmPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    public ChangePasswordDialog(UserService userService) {
        this.userService = userService;
        this.binder = new Binder<>(PasswordData.class);

        setHeaderTitle("修改密码");
        setWidth("500px");
        setResizable(true);
        setDraggable(true);

        // 创建表单字段
        oldPasswordField = new PasswordField("原密码");
        oldPasswordField.setRequired(true);
        oldPasswordField.setRequiredIndicatorVisible(true);
        oldPasswordField.setWidthFull();
        oldPasswordField.setPlaceholder("请输入原密码");

        newPasswordField = new PasswordField("新密码");
        newPasswordField.setRequired(true);
        newPasswordField.setRequiredIndicatorVisible(true);
        newPasswordField.setWidthFull();
        newPasswordField.setPlaceholder("请输入新密码，至少6个字符");
        newPasswordField.addValueChangeListener(e -> validatePasswordMatch());

        confirmPasswordField = new PasswordField("确认新密码");
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setRequiredIndicatorVisible(true);
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setPlaceholder("请再次输入新密码");
        confirmPasswordField.addValueChangeListener(e -> validatePasswordMatch());

        // 配置Binder验证
        binder.forField(oldPasswordField)
                .asRequired("原密码不能为空")
                .bind(PasswordData::getOldPassword, PasswordData::setOldPassword);

        binder.forField(newPasswordField)
                .asRequired("新密码不能为空")
                .withValidator(new StringLengthValidator("新密码长度必须在6-50个字符之间", 6, 50))
                .bind(PasswordData::getNewPassword, PasswordData::setNewPassword);

        binder.forField(confirmPasswordField)
                .asRequired("确认密码不能为空")
                .withValidator(new StringLengthValidator("确认密码长度必须在6-50个字符之间", 6, 50))
                .withValidator(password -> {
                    String newPassword = newPasswordField.getValue();
                    return newPassword != null && newPassword.equals(password);
                }, "两次输入的密码不一致")
                .bind(PasswordData::getConfirmPassword, PasswordData::setConfirmPassword);

        // 创建表单布局
        FormLayout formLayout = new FormLayout();
        formLayout.add(oldPasswordField, 2);
        formLayout.add(newPasswordField, 2);
        formLayout.add(confirmPasswordField, 2);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        // 创建按钮
        cancelButton = new Button("取消");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> close());

        saveButton = new Button("保存");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> savePassword());

        HorizontalLayout footerLayout = new HorizontalLayout(cancelButton, saveButton);
        footerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footerLayout.setSpacing(true);
        footerLayout.setWidthFull();
        footerLayout.setPadding(true);

        // 添加内容
        VerticalLayout content = new VerticalLayout(formLayout);
        content.setPadding(true);
        content.setSpacing(true);
        content.setWidthFull();

        add(content);
        getFooter().add(footerLayout);
    }

    /**
     * 验证密码匹配
     */
    private void validatePasswordMatch() {
        String newPassword = newPasswordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();

        if (newPassword != null && confirmPassword != null && !newPassword.isEmpty() && !confirmPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordField.setErrorMessage("两次输入的密码不一致");
                confirmPasswordField.setInvalid(true);
            } else {
                confirmPasswordField.setErrorMessage(null);
                confirmPasswordField.setInvalid(false);
            }
        }
    }

    /**
     * 保存密码
     */
    private void savePassword() {
        // 使用Binder验证
        PasswordData passwordData = new PasswordData();
        try {
            binder.writeBean(passwordData);
        } catch (com.vaadin.flow.data.binder.ValidationException e) {
            NotificationUtil.showError("请检查表单输入是否正确");
            return;
        }

        // 获取当前用户ID
        Long userId = UserUtil.getCurrentUserId();
        if (userId == null) {
            NotificationUtil.showError("无法获取当前用户信息");
            return;
        }

        try {
            // 调用服务修改密码
            userService.changePassword(
                    userId,
                    passwordData.getOldPassword(),
                    passwordData.getNewPassword()
            );

            NotificationUtil.showSuccess("密码修改成功");
            close();
        } catch (Exception e) {
            NotificationUtil.showError("修改密码失败：" + e.getMessage());
        }
    }
}

