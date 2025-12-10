package com.admin.views.base;

import com.admin.component.BaseFormDialog;
import com.admin.entity.BaseEntity;
import com.admin.util.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/**
 * 基础列表视图
 * 提供通用的列表视图功能，包括 Grid 配置、工具栏、操作列、删除确认等
 *
 * @param <T> 实体类型，必须继承 BaseEntity
 * @param <S> 服务类型
 * @author Admin
 * @date 2024-01-01
 */
public abstract class BaseListView<T extends BaseEntity, S> extends VerticalLayout {

    protected final S service;
    protected final Grid<T> grid;
    protected final String entityName;
    protected final String addButtonText;
    protected final String viewClassName;

    /**
     * 构造函数
     *
     * @param service        服务实例
     * @param entityClass    实体类
     * @param entityName     实体名称（用于显示）
     * @param addButtonText  添加按钮文本
     * @param viewClassName  视图 CSS 类名
     */
    public BaseListView(S service, Class<T> entityClass, String entityName, String addButtonText, String viewClassName) {
        this.service = service;
        this.entityName = entityName;
        this.addButtonText = addButtonText;
        this.viewClassName = viewClassName;
        this.grid = new Grid<>(entityClass, false);

        addClassName(viewClassName);
        setSizeFull();

        configureGrid();
        add(getToolbar(), grid);
        updateList();
    }

    /**
     * 配置 Grid
     */
    private void configureGrid() {
        grid.setSizeFull();
        grid.setWidthFull();
        configureColumns();
        addActionColumn();
//        addDoubleClickEdit();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
    }

    /**
     * 配置列
     * 子类需要实现此方法来配置具体的列
     */
    protected abstract void configureColumns();

    /**
     * 添加操作列
     */
    protected void addActionColumn() {
        grid.addComponentColumn(entity -> {
            HorizontalLayout actionLayout = new HorizontalLayout();
            actionLayout.setSpacing(true);

            Button editButton = new Button("编辑", new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> editEntity(entity));

            Button deleteButton = new Button("删除", new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> deleteEntity(entity));

            actionLayout.add(editButton, deleteButton);
            return actionLayout;
        }).setHeader("操作").setWidth("180px").setFlexGrow(0);
    }

    /**
     * 添加双击编辑功能
     */
    protected void addDoubleClickEdit() {
        grid.addItemDoubleClickListener(e -> {
            if (e.getItem() != null) {
                editEntity(e.getItem());
            }
        });
    }

    /**
     * 创建工具栏
     */
    protected HorizontalLayout getToolbar() {
        Button addButton = new Button(addButtonText);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> addEntity());

        Button refreshButton = new Button("刷新");
        refreshButton.addClickListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(addButton, refreshButton);
        toolbar.addClassName("toolbar");
        toolbar.setSpacing(true);
        return toolbar;
    }

    /**
     * 更新列表数据
     */
    protected void updateList() {
        try {
            List<T> items = getListData();
            grid.setItems(items);
        } catch (Exception e) {
            NotificationUtil.showError("加载" + entityName + "列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取列表数据
     * 子类需要实现此方法来获取数据
     *
     * @return 列表数据
     */
    protected abstract List<T> getListData();

    /**
     * 添加实体
     */
    protected void addEntity() {
        BaseFormDialog<T> dialog = getFormDialog(false, null);
        dialog.open();
    }

    /**
     * 编辑实体
     *
     * @param entity 实体对象
     */
    protected void editEntity(T entity) {
        BaseFormDialog<T> dialog = getFormDialog(true, entity);
        if (entity != null) {
            dialog.setEntity(entity);
        }
        dialog.open();
    }

    /**
     * 删除实体
     *
     * @param entity 实体对象
     */
    protected void deleteEntity(T entity) {
        String entityDisplayName = getEntityDisplayName(entity);
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("确认删除");
        confirmDialog.setText("确定要删除" + entityName + " \"" + entityDisplayName + "\" 吗？此操作不可恢复。");
        confirmDialog.setConfirmText("删除");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.setCancelText("取消");
        confirmDialog.setCancelButtonTheme("tertiary");
        confirmDialog.setCancelable(true);

        confirmDialog.addConfirmListener(e -> {
            try {
                performDelete(entity);
                NotificationUtil.showSuccess("删除" + entityName + "成功");
                updateList();
            } catch (Exception ex) {
                NotificationUtil.showError("删除" + entityName + "失败：" + ex.getMessage());
            }
        });

        confirmDialog.addCancelListener(e -> {
            // 用户点击取消，关闭对话框
        });

        confirmDialog.open();
    }

    /**
     * 获取表单对话框
     * 子类需要实现此方法来返回表单对话框
     *
     * @param isEdit 是否为编辑模式
     * @param entity 实体对象（编辑模式下使用）
     * @return 表单对话框
     */
    protected abstract BaseFormDialog<T> getFormDialog(boolean isEdit, T entity);

    /**
     * 执行删除操作
     * 子类需要实现此方法来执行实际的删除操作
     *
     * @param entity 实体对象
     */
    protected abstract void performDelete(T entity);

    /**
     * 获取实体显示名称
     * 用于删除确认对话框等场景
     * 默认返回实体的 toString()，子类可以重写
     *
     * @param entity 实体对象
     * @return 显示名称
     */
    protected String getEntityDisplayName(T entity) {
        if (entity == null) {
            return "";
        }
        // 尝试获取 name 字段
        try {
            java.lang.reflect.Method getNameMethod = entity.getClass().getMethod("getName");
            Object name = getNameMethod.invoke(entity);
            if (name != null) {
                return name.toString();
            }
        } catch (Exception e) {
            // 忽略异常，使用默认值
        }
        return entity.toString();
    }
}

