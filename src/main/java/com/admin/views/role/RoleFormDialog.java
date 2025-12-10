package com.admin.views.role;

import com.admin.component.BaseFormDialog;
import com.admin.entity.Role;
import com.admin.service.RoleService;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;

/**
 * 角色表单对话框
 * 用于新增和编辑角色
 *
 * @author Admin
 * @date 2024-01-01
 */
public class RoleFormDialog extends BaseFormDialog<Role> {

    private final RoleService roleService;
    private final Runnable refreshCallback;

    private TextField nameField;
    private TextField codeField;
    private TextField descriptionField;
    private Checkbox enabledCheckbox;

    /**
     * 构造函数
     *
     * @param roleService     角色服务
     * @param isEdit          是否为编辑模式
     * @param refreshCallback 刷新回调
     */
    public RoleFormDialog(RoleService roleService, boolean isEdit, Runnable refreshCallback) {
        super(Role.class, isEdit);
        this.roleService = roleService;
        this.refreshCallback = refreshCallback;
        // 设置对话框标题
        if (isEdit) {
            setHeaderTitle("编辑角色");
        } else {
            setHeaderTitle("新增角色");
        }
    }

    @Override
    protected void buildFormFields() {
        nameField = new TextField("角色名称");
        nameField.setRequired(true);
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();
        nameField.setPlaceholder("请输入角色名称，1-50个字符");

        codeField = new TextField("角色编码");
        codeField.setRequired(true);
        codeField.setRequiredIndicatorVisible(true);
        codeField.setWidthFull();
        codeField.setPlaceholder("请输入角色编码，1-50个字符，唯一标识");
        if (isEdit) {
            codeField.setReadOnly(true);
            codeField.setHelperText("编辑模式下编码不可修改");
        }

        descriptionField = new TextField("描述");
        descriptionField.setWidthFull();
        descriptionField.setPlaceholder("请输入角色描述，最多200个字符");

        enabledCheckbox = new Checkbox("是否启用");
        enabledCheckbox.setValue(true);

        formLayout.add(nameField, 2);
        formLayout.add(codeField, 2);
        formLayout.add(descriptionField, 2);
        formLayout.add(enabledCheckbox, 2);
    }

    @Override
    protected void configureBinder() {
        // 手动绑定字段
        binder.forField(nameField)
                .asRequired("角色名称不能为空")
                .withValidator(new StringLengthValidator("角色名称长度必须在1-50个字符之间", 1, 50))
                .bind(Role::getName, Role::setName);

        binder.forField(codeField)
                .asRequired("角色编码不能为空")
                .withValidator(new StringLengthValidator("角色编码长度必须在1-50个字符之间", 1, 50))
                .withValidator(code -> {
                    if (isEdit) {
                        // 编辑模式下，编码唯一性由Service层验证（已排除当前记录）
                        return true;
                    }
                    // 新增模式下检查编码是否已存在
                    return !isCodeExists(code);
                }, "角色编码已存在，请使用其他编码")
                .bind(Role::getCode, Role::setCode);

        binder.forField(descriptionField)
                .withValidator(description -> description == null || description.length() <= 200,
                        "描述长度不能超过200个字符")
                .bind(Role::getDescription, Role::setDescription);

        binder.forField(enabledCheckbox)
                .bind(Role::getIsEnabled, Role::setIsEnabled);
    }

    @Override
    protected void loadEntityData() {
        // 编辑模式下，数据通过 setEntity 方法设置
    }

    @Override
    protected void copyEntityFields(Role source, Role target) {
        target.setName(source.getName());
        target.setCode(source.getCode());
        target.setDescription(source.getDescription());
        target.setIsEnabled(source.getIsEnabled());
        target.setDeleted(source.getDeleted());
    }

    @Override
    protected void save() {
        if (!validateAndWrite()) {
            return;
        }

        try {
            if (isEdit) {
                roleService.updateRole(entity);
                showSuccessAndClose("更新角色成功");
            } else {
                roleService.saveRole(entity);
                showSuccessAndClose("保存角色成功");
            }
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } catch (com.admin.exception.BusinessException e) {
            // 业务异常，显示友好的错误信息
            showError(e.getMessage());
        } catch (Exception e) {
            // 其他异常，显示通用错误信息
            showError("操作失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 检查角色编码是否已存在
     *
     * @param code 角色编码
     * @return 是否存在
     */
    private boolean isCodeExists(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        Role existRole = roleService.getRoleByCode(code);
        return existRole != null;
    }
}

