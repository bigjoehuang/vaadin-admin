package com.admin.views.menu;

import com.admin.component.BaseFormDialog;
import com.admin.entity.Menu;
import com.admin.service.MenuService;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单表单对话框
 * 用于新增和编辑菜单
 *
 * @author Admin
 * @date 2024-01-01
 */
public class MenuFormDialog extends BaseFormDialog<Menu> {

    private final MenuService menuService;
    private final Runnable refreshCallback;

    private TextField nameField;
    private TextField pathField;
    private TextField componentField;
    private TextField iconField;
    private ComboBox<Menu> parentMenuComboBox;
    private IntegerField sortField;
    private Checkbox enabledCheckbox;

    /**
     * 构造函数
     *
     * @param menuService     菜单服务
     * @param isEdit          是否为编辑模式
     * @param refreshCallback 刷新回调
     */
    public MenuFormDialog(MenuService menuService, boolean isEdit, Runnable refreshCallback) {
        super(Menu.class, isEdit);
        this.menuService = menuService;
        this.refreshCallback = refreshCallback;
        // 设置对话框标题
        if (isEdit) {
            setHeaderTitle("编辑菜单");
        } else {
            setHeaderTitle("新增菜单");
        }
    }

    @Override
    protected void buildFormFields() {
        nameField = new TextField("菜单名称");
        nameField.setRequired(true);
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();
        nameField.setPlaceholder("请输入菜单名称，1-50个字符");
        nameField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        nameField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        pathField = new TextField("菜单路径");
        pathField.setWidthFull();
        pathField.setPlaceholder("请输入菜单路径，如：/users");
        pathField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        pathField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        componentField = new TextField("组件路径");
        componentField.setWidthFull();
        componentField.setPlaceholder("请输入组件路径，如：views/user/UserListView");
        componentField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        componentField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        iconField = new TextField("图标");
        iconField.setWidthFull();
        iconField.setPlaceholder("请输入图标名称，如：vaadin:user");
        iconField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        iconField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        // 父菜单选择
        parentMenuComboBox = new ComboBox<>("父菜单");
        parentMenuComboBox.setWidthFull();
        parentMenuComboBox.setPlaceholder("请选择父菜单（可选）");
        parentMenuComboBox.setItemLabelGenerator(menu -> menu.getName() != null ? menu.getName() : "");
        parentMenuComboBox.setClearButtonVisible(true);
        // 加载菜单列表
        loadParentMenuOptions();

        sortField = new IntegerField("排序");
        sortField.setWidthFull();
        sortField.setPlaceholder("请输入排序值，数字越小越靠前");
        sortField.setValue(0);
        sortField.setMin(0);
        sortField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        sortField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        enabledCheckbox = new Checkbox("是否启用");
        enabledCheckbox.setValue(true);

        formLayout.add(nameField, 2);
        formLayout.add(pathField, 2);
        formLayout.add(componentField, 2);
        formLayout.add(iconField, 2);
        formLayout.add(parentMenuComboBox, 2);
        formLayout.add(sortField, 2);
        formLayout.add(enabledCheckbox, 2);
    }

    /**
     * 加载父菜单选项
     */
    private void loadParentMenuOptions() {
        List<Menu> allMenus = menuService.listMenus();
        
        // 如果是编辑模式，排除当前菜单（避免循环引用）
        if (isEdit && entity != null && entity.getId() != null) {
            allMenus = allMenus.stream()
                    .filter(menu -> !menu.getId().equals(entity.getId()))
                    .collect(Collectors.toList());
        }
        
        // 添加"无父菜单"选项（使用null表示）
        List<Menu> options = new ArrayList<>();
        options.add(null); // 第一个选项为null，表示无父菜单
        options.addAll(allMenus);
        
        parentMenuComboBox.setItems(options);
        
        // 设置null选项的显示文本
        parentMenuComboBox.setItemLabelGenerator(menu -> {
            if (menu == null) {
                return "无父菜单（根菜单）";
            }
            return menu.getName() != null ? menu.getName() : "";
        });
    }

    @Override
    protected void configureBinder() {
        // 手动绑定字段
        binder.forField(nameField)
                .asRequired("菜单名称不能为空")
                .withValidator(new StringLengthValidator("菜单名称长度必须在1-50个字符之间", 1, 50))
                .bind(Menu::getName, Menu::setName);

        binder.forField(pathField)
                .withValidator(path -> path == null || path.length() <= 200,
                        "路径长度不能超过200个字符")
                .bind(Menu::getPath, Menu::setPath);

        binder.forField(componentField)
                .withValidator(component -> component == null || component.length() <= 200,
                        "组件路径长度不能超过200个字符")
                .bind(Menu::getComponent, Menu::setComponent);

        binder.forField(iconField)
                .withValidator(icon -> icon == null || icon.length() <= 100,
                        "图标名称长度不能超过100个字符")
                .bind(Menu::getIcon, Menu::setIcon);

        binder.forField(parentMenuComboBox)
                .bind(menu -> {
                    // 从Menu对象获取parentId对应的Menu对象
                    if (menu == null || menu.getParentId() == null) {
                        return null;
                    }
                    return menuService.getMenuById(menu.getParentId());
                }, (menu, parentMenu) -> {
                    // 设置parentId
                    if (parentMenu == null) {
                        menu.setParentId(null);
                    } else {
                        menu.setParentId(parentMenu.getId());
                    }
                });

        binder.forField(sortField)
                .withValidator(sort -> sort != null && sort >= 0,
                        "排序值必须大于等于0")
                .bind(menu -> menu.getSort() != null ? menu.getSort() : 0,
                        (menu, sort) -> menu.setSort(sort != null ? sort : 0));

        binder.forField(enabledCheckbox)
                .bind(Menu::getIsEnabled, Menu::setIsEnabled);
    }

    @Override
    protected void loadEntityData() {
        // 编辑模式下，数据通过 setEntity 方法设置
        // 重新加载父菜单选项（排除当前菜单）
        if (isEdit) {
            loadParentMenuOptions();
        }
    }

    @Override
    protected void copyEntityFields(Menu source, Menu target) {
        target.setName(source.getName());
        target.setPath(source.getPath());
        target.setComponent(source.getComponent());
        target.setIcon(source.getIcon());
        target.setParentId(source.getParentId());
        target.setSort(source.getSort());
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
                menuService.updateMenu(entity);
                showSuccessAndClose("更新菜单成功");
            } else {
                menuService.saveMenu(entity);
                showSuccessAndClose("保存菜单成功");
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

    @Override
    public void setEntity(Menu entity) {
        super.setEntity(entity);
        // 重新加载父菜单选项（排除当前菜单）
        if (isEdit && entity != null) {
            loadParentMenuOptions();
        }
    }
}

