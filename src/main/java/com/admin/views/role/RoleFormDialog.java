package com.admin.views.role;

import com.admin.component.BaseFormDialog;
import com.admin.entity.Role;
import com.admin.service.RoleService;
import com.admin.util.I18NUtil;
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
            setHeaderTitle(I18NUtil.get("role.edit"));
        } else {
            setHeaderTitle(I18NUtil.get("role.new"));
        }
    }

    @Override
    protected void buildFormFields() {
        nameField = new TextField(I18NUtil.get("role.name"));
        nameField.setRequired(true);
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();
        nameField.setPlaceholder(I18NUtil.get("role.placeholder.name.input"));
        // 通过Java代码设置label颜色，确保在获得焦点时可见
        nameField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        nameField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        codeField = new TextField(I18NUtil.get("role.code"));
        codeField.setRequired(true);
        codeField.setRequiredIndicatorVisible(true);
        codeField.setWidthFull();
        codeField.setPlaceholder(I18NUtil.get("role.placeholder.code.input"));
        // 通过Java代码设置label颜色
        codeField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        codeField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");
        if (isEdit) {
            codeField.setReadOnly(true);
            codeField.setHelperText(I18NUtil.get("role.helper.code.edit"));
        }

        descriptionField = new TextField(I18NUtil.get("role.description"));
        descriptionField.setWidthFull();
        descriptionField.setPlaceholder(I18NUtil.get("role.placeholder.description"));
        // 通过Java代码设置label颜色
        descriptionField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        descriptionField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        enabledCheckbox = new Checkbox(I18NUtil.get("role.status"));
        enabledCheckbox.setValue(true);

        formLayout.add(nameField, 2);
        formLayout.add(codeField, 2);
        formLayout.add(descriptionField, 2);
        formLayout.add(enabledCheckbox, 2);
        
        // 添加调试信息：检查样式是否正确应用
        addDebugInfo();
    }
    
    /**
     * 添加调试信息，检查样式是否正确应用
     */
    private void addDebugInfo() {
        // 延迟执行，确保DOM已渲染
        getUI().ifPresent(ui -> ui.getPage().executeJs(
            "console.log('=== RoleFormDialog 样式调试信息 ===');" +
            "setTimeout(function() {" +
            "  var textFields = document.querySelectorAll('vaadin-text-field');" +
            "  console.log('找到 ' + textFields.length + ' 个 TextField');" +
            "  textFields.forEach(function(field, index) {" +
            "    var label = field.shadowRoot ? field.shadowRoot.querySelector('label') : null;" +
            "    var labelText = label ? label.textContent : 'N/A';" +
            "    var labelColor = label ? window.getComputedStyle(label).color : 'N/A';" +
            "    var labelPart = field.shadowRoot ? field.shadowRoot.querySelector('[part=\"label\"]') : null;" +
            "    var labelPartColor = labelPart ? window.getComputedStyle(labelPart).color : 'N/A';" +
            "    var cssVar = window.getComputedStyle(field).getPropertyValue('--lumo-text-field-label-color');" +
            "    var vaadinVar = window.getComputedStyle(field).getPropertyValue('--vaadin-input-field-label-color');" +
            "    console.log('TextField[' + index + ']:');" +
            "    console.log('  Label文本: ' + labelText);" +
            "    console.log('  Label颜色: ' + labelColor);" +
            "    console.log('  Label Part颜色: ' + labelPartColor);" +
            "    console.log('  --lumo-text-field-label-color: ' + (cssVar || '未设置'));" +
            "    console.log('  --vaadin-input-field-label-color: ' + (vaadinVar || '未设置'));" +
            "    console.log('  是否有shadowRoot: ' + (field.shadowRoot ? '是' : '否'));" +
            "    if (field.shadowRoot) {" +
            "      var labelElement = field.shadowRoot.querySelector('label');" +
            "      if (labelElement) {" +
            "        console.log('  Label元素类名: ' + labelElement.className);" +
            "        console.log('  Label元素样式: ' + labelElement.getAttribute('style'));" +
            "      }" +
            "    }" +
            "  });" +
            "  console.log('=== 调试信息结束 ===');" +
            "}, 500);"
        ));
    }

    @Override
    protected void configureBinder() {
        // 手动绑定字段
        binder.forField(nameField)
                .asRequired(I18NUtil.get("role.validation.name.required"))
                .withValidator(new StringLengthValidator(I18NUtil.get("role.validation.name.length"), 1, 50))
                .bind(Role::getName, Role::setName);

        binder.forField(codeField)
                .asRequired(I18NUtil.get("role.validation.code.required"))
                .withValidator(new StringLengthValidator(I18NUtil.get("role.validation.code.length"), 1, 50))
                .withValidator(code -> {
                    if (isEdit) {
                        // 编辑模式下，编码唯一性由Service层验证（已排除当前记录）
                        return true;
                    }
                    // 新增模式下检查编码是否已存在
                    return !isCodeExists(code);
                }, I18NUtil.get("role.validation.code.exists"))
                .bind(Role::getCode, Role::setCode);

        binder.forField(descriptionField)
                .withValidator(description -> description == null || description.length() <= 200,
                        I18NUtil.get("role.validation.description.length"))
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
                showSuccessAndClose(I18NUtil.get("role.update.success"));
            } else {
                roleService.saveRole(entity);
                showSuccessAndClose(I18NUtil.get("role.save.success"));
            }
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } catch (com.admin.exception.BusinessException e) {
            // 业务异常，显示友好的错误信息
            showError(e.getMessage());
        } catch (Exception e) {
            // 其他异常，显示通用错误信息
            showError(I18NUtil.get("error.operation.failed") + ": " + e.getMessage());
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

