package com.admin.component;

import com.admin.entity.BaseEntity;
import com.admin.util.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

/**
 * 基础表单对话框
 * 提供通用的表单对话框功能，包括保存和取消按钮
 *
 * @param <T> 实体类型，必须继承 BaseEntity
 * @author Admin
 * @date 2024-01-01
 */
public abstract class BaseFormDialog<T extends BaseEntity> extends Dialog {

    protected final Binder<T> binder;
    protected final T entity;
    protected final boolean isEdit;
    protected final Button saveButton;
    protected final Button cancelButton;
    protected final FormLayout formLayout;

    /**
     * 构造函数
     *
     * @param entityClass 实体类
     * @param isEdit      是否为编辑模式
     */
    public BaseFormDialog(Class<T> entityClass, boolean isEdit) {
        this.isEdit = isEdit;
        this.binder = new Binder<>(entityClass);
        
        try {
            this.entity = entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("无法创建实体实例", e);
        }

        setHeaderTitle(isEdit ? "编辑" : "新增");
        // 注意：编辑模式下的标题可以在子类中通过 setHeaderTitle 方法重新设置
        setWidth("600px");
        setResizable(true);
        setDraggable(true);

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        // 构建表单字段
        buildFormFields();

        // 绑定验证器
        configureBinder();

        // 如果是编辑模式，设置实体数据
        if (isEdit) {
            loadEntityData();
        }

        // 创建按钮
        cancelButton = new Button("取消");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> close());
        // 确保取消按钮文字可见
        cancelButton.getElement().getStyle().set("color", "var(--lumo-body-text-color)");

        saveButton = new Button(isEdit ? "更新" : "保存");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());
        // 主要按钮文字颜色（使用CSS变量，已改为黑色）
        saveButton.getElement().getStyle().set("color", "var(--lumo-primary-text-color)");

        // 将按钮添加到对话框的 footer
        HorizontalLayout footerLayout = new HorizontalLayout(cancelButton, saveButton);
        footerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footerLayout.setSpacing(true);
        footerLayout.setWidthFull();
        footerLayout.setPadding(true);
        getFooter().add(footerLayout);

        // 添加表单内容
        VerticalLayout content = new VerticalLayout(formLayout);
        content.setPadding(true);
        content.setSpacing(true);
        content.setWidthFull();

        add(content);
        
        // 添加全局调试信息：检查CSS变量
        addGlobalDebugInfo();
    }
    
    /**
     * 添加全局调试信息
     */
    private void addGlobalDebugInfo() {
        getUI().ifPresent(ui -> ui.getPage().executeJs(
            "console.log('=== BaseFormDialog 全局样式调试 ===');" +
            "setTimeout(function() {" +
            "  var root = document.documentElement;" +
            "  var lumoVar = window.getComputedStyle(root).getPropertyValue('--lumo-text-field-label-color');" +
            "  var vaadinVar = window.getComputedStyle(root).getPropertyValue('--vaadin-input-field-label-color');" +
            "  var bodyTextColor = window.getComputedStyle(root).getPropertyValue('--lumo-body-text-color');" +
            "  console.log('全局CSS变量:');" +
            "  console.log('  --lumo-text-field-label-color: ' + (lumoVar || '未设置'));" +
            "  console.log('  --vaadin-input-field-label-color: ' + (vaadinVar || '未设置'));" +
            "  console.log('  --lumo-body-text-color: ' + (bodyTextColor || '未设置'));" +
            "  console.log('=== 全局样式调试结束 ===');" +
            "}, 300);"
        ));
    }

    /**
     * 设置实体数据（用于编辑模式）
     *
     * @param entity 实体对象
     */
    public void setEntity(T entity) {
        if (entity != null) {
            this.entity.setId(entity.getId());
            this.entity.setCreatedAt(entity.getCreatedAt());
            this.entity.setUpdatedAt(entity.getUpdatedAt());
            copyEntityFields(entity, this.entity);
            binder.readBean(this.entity);
        }
    }

    /**
     * 构建表单字段
     * 子类需要实现此方法来添加具体的表单字段
     */
    protected abstract void buildFormFields();

    /**
     * 配置绑定器
     * 子类可以重写此方法来添加验证规则
     */
    protected void configureBinder() {
        // 默认实现，子类可以重写
    }

    /**
     * 加载实体数据
     * 编辑模式下，子类需要实现此方法来加载数据
     */
    protected void loadEntityData() {
        // 默认实现，子类可以重写
    }

    /**
     * 复制实体字段
     * 子类需要实现此方法来复制实体字段
     *
     * @param source 源实体
     * @param target 目标实体
     */
    protected abstract void copyEntityFields(T source, T target);

    /**
     * 保存数据
     * 子类需要实现此方法来执行保存操作
     */
    protected abstract void save();

    /**
     * 验证并写入数据到实体
     *
     * @return 是否验证通过
     */
    protected boolean validateAndWrite() {
        try {
            binder.writeBean(entity);
            return true;
        } catch (ValidationException e) {
            NotificationUtil.showError("请检查表单输入是否正确");
            return false;
        }
    }

    /**
     * 显示成功消息并关闭对话框
     *
     * @param message 成功消息
     */
    protected void showSuccessAndClose(String message) {
        NotificationUtil.showSuccess(message);
        close();
    }

    /**
     * 显示错误消息
     *
     * @param message 错误消息
     */
    protected void showError(String message) {
        NotificationUtil.showError(message);
    }
}

