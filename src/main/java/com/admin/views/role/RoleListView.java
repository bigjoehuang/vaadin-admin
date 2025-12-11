package com.admin.views.role;

import com.admin.component.BaseFormDialog;
import com.admin.component.ConfirmDialogUtil;
import com.admin.constant.StatusConstant;
import com.admin.dto.PageRequest;
import com.admin.dto.RoleQueryDTO;
import com.admin.entity.Role;
import com.admin.service.PermissionService;
import com.admin.service.RoleService;
import com.admin.util.DataProviderUtil;
import com.admin.util.I18NUtil;
import com.admin.util.NotificationUtil;
import com.admin.util.PageResult;
import com.admin.util.PaginationUtil;
import com.admin.views.MainLayout;
import com.admin.views.base.BaseListView;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "roles", layout = MainLayout.class)
public class RoleListView extends BaseListView<Role, RoleService> implements HasDynamicTitle {

    // 搜索和筛选组件
    private TextField nameSearchField;
    private TextField codeSearchField;
    private ComboBox<String> statusFilter;
    private Button searchButton;
    private Button resetButton;

    // 状态筛选器使用常量值，显示时使用 I18N 文本

    // 批量操作组件
    private Button batchDeleteButton;
    private Button batchEnableButton;
    private Button batchDisableButton;

    // 分页组件
    private HorizontalLayout paginationLayout;
    private Button firstPageButton;
    private Button prevPageButton;
    private Button nextPageButton;
    private Button lastPageButton;
    private Span pageInfo;

    // 查询条件
    private RoleQueryDTO currentQuery = new RoleQueryDTO();
    private PageRequest currentPageRequest = new PageRequest();

    // 当前分页数据
    private PageResult<Role> currentPageResult;
    
    // DataProvider
    private DataProvider<Role, Void> dataProvider;
    
    private final PermissionService permissionService;

    public RoleListView(RoleService roleService, PermissionService permissionService) {
        super(roleService, Role.class, I18NUtil.get("role.title"), I18NUtil.get("role.add"), "role-list-view");
        this.permissionService = permissionService;
        
        // 启用Grid多选模式
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        
        // 初始化 DataProvider
        initDataProvider();
        
        // 重新构建布局
        removeAll();
        add(buildToolbar(), buildSearchBar(), buildBatchOperationBar(), grid, buildPagination());
        
        // 初始化查询
        performSearch();
    }

    /**
     * 重写此方法，避免重复添加操作列（已在configureColumns中添加）
     */
    @Override
    protected void addActionColumn() {
        // 操作列已在configureColumns中添加，这里不需要重复添加
    }

    @Override
    protected void configureColumns() {
        grid.removeAllColumns();
        
        // 添加选择列（多选）
        grid.addColumn(role -> "").setHeader("").setWidth("30px").setFlexGrow(0);
        
        grid.addColumn(Role::getId).setHeader("ID").setWidth("80px").setFlexGrow(0).setSortable(true);
        grid.addColumn(Role::getName).setHeader(I18NUtil.get("role.name")).setFlexGrow(1);
        grid.addColumn(Role::getCode).setHeader(I18NUtil.get("role.code")).setFlexGrow(1);
        grid.addColumn(Role::getDescription).setHeader(I18NUtil.get("role.description")).setFlexGrow(2);
        
        // 优化状态列显示（使用图标和颜色）
        grid.addComponentColumn(role -> {
            boolean enabled = role.getIsEnabled() != null && role.getIsEnabled();
            
            // 创建容器
            HorizontalLayout statusLayout = new HorizontalLayout();
            statusLayout.setSpacing(true);
            statusLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            statusLayout.setPadding(false);
            statusLayout.setMargin(false);
            
            // 创建图标
            Icon icon = new Icon(enabled ? VaadinIcon.CHECK_CIRCLE : VaadinIcon.CLOSE_CIRCLE);
            icon.setSize("16px");
            if (enabled) {
                icon.getStyle().set("color", "var(--lumo-success-color)");
            } else {
                icon.getStyle().set("color", "var(--lumo-error-color)");
            }
            
            // 创建文本
            Span textSpan = new Span(enabled ? I18NUtil.get("role.enabled") : I18NUtil.get("role.disabled"));
            textSpan.getStyle().set("font-size", "var(--lumo-font-size-s)");
            
            statusLayout.add(icon, textSpan);
            statusLayout.setFlexGrow(0, icon);
            statusLayout.setFlexGrow(1, textSpan);
            
            return statusLayout;
        }).setHeader(I18NUtil.get("role.status")).setWidth("100px").setFlexGrow(0);
        
        grid.addColumn(Role::getCreatedAt).setHeader(I18NUtil.get("role.createdAt")).setWidth("180px").setFlexGrow(0).setSortable(true);
        grid.addColumn(Role::getUpdatedAt).setHeader(I18NUtil.get("role.updatedAt")).setWidth("180px").setFlexGrow(0).setSortable(true);
        
        // 添加操作列
        grid.addComponentColumn(role -> {
            HorizontalLayout actionLayout = new HorizontalLayout();
            actionLayout.setSpacing(true);

            Button editButton = new Button(I18NUtil.get("common.edit"), new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> editEntity(role));

            Button deleteButton = new Button(I18NUtil.get("common.delete"), new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> deleteEntity(role));

            Button assignPermissionButton = new Button(I18NUtil.get("role.assign.permission"), new Icon(VaadinIcon.KEY));
            assignPermissionButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
            assignPermissionButton.addClickListener(e -> {
                RolePermissionAssignDialog dialog = new RolePermissionAssignDialog(
                    service, 
                    permissionService, 
                    role, 
                    this::performSearch
                );
                dialog.open();
            });

            actionLayout.add(editButton, deleteButton, assignPermissionButton);
            return actionLayout;
        }).setHeader(I18NUtil.get("role.operation")).setWidth("280px").setFlexGrow(0);
    }

    @Override
    protected List<Role> getListData() {
        // 使用 DataProvider 懒加载，这里返回空列表
        return new ArrayList<>();
    }
    
    /**
     * 初始化 DataProvider
     */
    private void initDataProvider() {
        // 确保分页请求已初始化
        if (currentPageRequest == null) {
            currentPageRequest = new PageRequest();
        }
        
        // 设置 Grid 的 pageSize 与分页请求一致
        grid.setPageSize(currentPageRequest.getPageSize());
        
        dataProvider = DataProviderUtil.createPageDataProvider(
            () -> currentQuery != null ? currentQuery : new RoleQueryDTO(),
            () -> {
                // 确保返回的分页请求不为 null
                if (currentPageRequest == null) {
                    currentPageRequest = new PageRequest();
                }
                return currentPageRequest;
            },
            service::pageRoles
        );
        grid.setDataProvider(dataProvider);
    }

    @Override
    protected BaseFormDialog<Role> getFormDialog(boolean isEdit, Role entity) {
        RoleFormDialog dialog = new RoleFormDialog(service, isEdit, this::performSearch);
        if (isEdit && entity != null) {
            dialog.setEntity(entity);
        }
        return dialog;
    }

    @Override
    protected void performDelete(Role entity) {
        service.deleteRole(entity.getId());
    }

    /**
     * 构建工具栏
     */
    private HorizontalLayout buildToolbar() {
        Button addButton = new Button(I18NUtil.get("role.add"), new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> addEntity());

        Button refreshButton = new Button(I18NUtil.get("common.refresh"), new Icon(VaadinIcon.REFRESH));
        refreshButton.addClickListener(e -> performSearch());

        HorizontalLayout toolbar = new HorizontalLayout(addButton, refreshButton);
        toolbar.addClassName("toolbar");
        toolbar.setSpacing(true);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        return toolbar;
    }

    /**
     * 构建搜索栏
     */
    private HorizontalLayout buildSearchBar() {
        nameSearchField = new TextField(I18NUtil.get("role.name"));
        nameSearchField.setPlaceholder(I18NUtil.get("role.placeholder.name"));
        nameSearchField.setWidth("200px");
        nameSearchField.setClearButtonVisible(true);
        // 通过Java代码设置label颜色，确保在获得焦点时可见
        nameSearchField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        nameSearchField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        codeSearchField = new TextField(I18NUtil.get("role.code"));
        codeSearchField.setPlaceholder(I18NUtil.get("role.placeholder.code"));
        codeSearchField.setWidth("200px");
        codeSearchField.setClearButtonVisible(true);
        // 通过Java代码设置label颜色
        codeSearchField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        codeSearchField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        statusFilter = new ComboBox<>(I18NUtil.get("role.status"));
        // 使用常量值作为选项值，避免使用 I18N 文本进行比较
        statusFilter.setItems(StatusConstant.ALL, StatusConstant.ENABLED, StatusConstant.DISABLED);
        // 设置显示文本生成器，使用 I18N 文本显示
        statusFilter.setItemLabelGenerator(status -> {
            if (StatusConstant.ALL.equals(status)) {
                return I18NUtil.get("common.all");
            } else if (StatusConstant.ENABLED.equals(status)) {
                return I18NUtil.get("role.enabled");
            } else if (StatusConstant.DISABLED.equals(status)) {
                return I18NUtil.get("role.disabled");
            }
            return status;
        });
        statusFilter.setValue(StatusConstant.ALL);
        statusFilter.setWidth("120px");
        statusFilter.setClearButtonVisible(true); // 启用清除按钮
        statusFilter.setPlaceholder(I18NUtil.get("role.placeholder.status")); // 设置占位符

        searchButton = new Button(I18NUtil.get("common.search"), new Icon(VaadinIcon.SEARCH));
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(e -> performSearch());

        resetButton = new Button(I18NUtil.get("common.reset"), new Icon(VaadinIcon.REFRESH));
        resetButton.addClickListener(e -> resetSearch());

        HorizontalLayout searchBar = new HorizontalLayout(
                nameSearchField, codeSearchField, statusFilter, searchButton, resetButton
        );
        searchBar.setSpacing(true);
        searchBar.setAlignItems(FlexComponent.Alignment.END);
        searchBar.setWidthFull();
        searchBar.addClassName("search-bar");
        
        // 添加调试信息：检查样式是否正确应用
        addSearchBarDebugInfo();
        
        return searchBar;
    }
    
    /**
     * 添加搜索栏调试信息
     */
    private void addSearchBarDebugInfo() {
        getUI().ifPresent(ui -> ui.getPage().executeJs(
            "console.log('=== RoleListView 搜索栏样式调试信息 ===');" +
            "setTimeout(function() {" +
            "  var nameField = document.querySelector('vaadin-text-field[placeholder=\"请输入角色名称\"]');" +
            "  var codeField = document.querySelector('vaadin-text-field[placeholder=\"请输入角色编码\"]');" +
            "  " +
            "  function debugTextField(field, name) {" +
            "    if (!field) {" +
            "      console.log(name + ': 未找到元素');" +
            "      return;" +
            "    }" +
            "    console.log(name + ':');" +
            "    console.log('  元素标签: ' + field.tagName);" +
            "    console.log('  是否有shadowRoot: ' + (field.shadowRoot ? '是' : '否'));" +
            "    var cssVar = window.getComputedStyle(field).getPropertyValue('--lumo-text-field-label-color');" +
            "    var vaadinVar = window.getComputedStyle(field).getPropertyValue('--vaadin-input-field-label-color');" +
            "    console.log('  --lumo-text-field-label-color: ' + (cssVar || '未设置'));" +
            "    console.log('  --vaadin-input-field-label-color: ' + (vaadinVar || '未设置'));" +
            "    if (field.shadowRoot) {" +
            "      var label = field.shadowRoot.querySelector('label');" +
            "      if (label) {" +
            "        var labelColor = window.getComputedStyle(label).color;" +
            "        console.log('  Label元素: ' + label.tagName);" +
            "        console.log('  Label颜色: ' + labelColor);" +
            "        console.log('  Label类名: ' + label.className);" +
            "        console.log('  Label内联样式: ' + (label.getAttribute('style') || '无'));" +
            "      } else {" +
            "        console.log('  Label元素: 未找到');" +
            "      }" +
            "      var labelPart = field.shadowRoot.querySelector('[part=\"label\"]');" +
            "      if (labelPart) {" +
            "        var labelPartColor = window.getComputedStyle(labelPart).color;" +
            "        console.log('  Label Part颜色: ' + labelPartColor);" +
            "      }" +
            "    }" +
            "  }" +
            "  " +
            "  debugTextField(nameField, 'nameSearchField');" +
            "  debugTextField(codeField, 'codeSearchField');" +
            "  console.log('=== 搜索栏调试信息结束 ===');" +
            "}, 500);"
        ));
    }

    /**
     * 构建批量操作栏
     */
    private HorizontalLayout buildBatchOperationBar() {
        batchDeleteButton = new Button(I18NUtil.get("role.batch.delete"), new Icon(VaadinIcon.TRASH));
        batchDeleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        batchDeleteButton.addClickListener(e -> performBatchDelete());

        batchEnableButton = new Button(I18NUtil.get("role.batch.enable"), new Icon(VaadinIcon.CHECK));
        batchEnableButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        batchEnableButton.addClickListener(e -> performBatchUpdateStatus(true));

        batchDisableButton = new Button(I18NUtil.get("role.batch.disable"), new Icon(VaadinIcon.CLOSE));
        batchDisableButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        batchDisableButton.addClickListener(e -> performBatchUpdateStatus(false));

        HorizontalLayout batchBar = new HorizontalLayout(
                batchDeleteButton, batchEnableButton, batchDisableButton
        );
        batchBar.setSpacing(true);
        batchBar.setVisible(false); // 默认隐藏，有选中项时显示
        batchBar.addClassName("batch-operation-bar");
        return batchBar;
    }

    /**
     * 构建分页组件
     */
    private HorizontalLayout buildPagination() {
        firstPageButton = new Button(I18NUtil.get("common.firstPage"), new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
        firstPageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        firstPageButton.addClickListener(e -> {
            currentPageRequest.setPageNum(1);
            performSearch();
        });

        prevPageButton = new Button(I18NUtil.get("common.prevPage"), new Icon(VaadinIcon.ANGLE_LEFT));
        prevPageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        prevPageButton.addClickListener(e -> {
            if (currentPageRequest.getPageNum() > 1) {
                currentPageRequest.setPageNum(currentPageRequest.getPageNum() - 1);
                performSearch();
            }
        });

        nextPageButton = new Button(I18NUtil.get("common.nextPage"), new Icon(VaadinIcon.ANGLE_RIGHT));
        nextPageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        nextPageButton.addClickListener(e -> {
            if (currentPageResult != null && currentPageResult.getData() != null) {
                int totalPages = PaginationUtil.calculateTotalPages(currentPageResult.getData());
                if (PaginationUtil.hasNextPage(currentPageRequest.getPageNum(), totalPages)) {
                    currentPageRequest.setPageNum(currentPageRequest.getPageNum() + 1);
                    performSearch();
                }
            }
        });

        lastPageButton = new Button(I18NUtil.get("common.lastPage"), new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
        lastPageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        lastPageButton.addClickListener(e -> {
            if (currentPageResult != null && currentPageResult.getData() != null) {
                currentPageRequest.setPageNum(PaginationUtil.getLastPageNum(currentPageResult.getData()));
                performSearch();
            }
        });

        pageInfo = new Span();
        pageInfo.getStyle().set("margin", "0 16px");
        pageInfo.getStyle().set("align-self", "center");

        paginationLayout = new HorizontalLayout(
                firstPageButton, prevPageButton, pageInfo, nextPageButton, lastPageButton
        );
        paginationLayout.setWidthFull();
        paginationLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        paginationLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        paginationLayout.setPadding(true);
        return paginationLayout;
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        try {
            // 确保分页请求不为null
            if (currentPageRequest == null) {
                currentPageRequest = new PageRequest();
            }
            
            // 检查字段是否已初始化（防止在父类构造函数调用时出现空指针）
            if (nameSearchField == null || codeSearchField == null || statusFilter == null) {
                // 如果字段未初始化，使用默认查询（查询所有数据）
                currentQuery = new RoleQueryDTO();
            } else {
                // 构建查询条件
                buildQuery();
            }
            
            // 确保查询条件不为null
            if (currentQuery == null) {
                currentQuery = new RoleQueryDTO();
            }

            // 刷新 DataProvider（懒加载会自动从 Service 获取数据）
            if (dataProvider != null) {
                dataProvider.refreshAll();
            }
            
            // 获取当前页数据用于更新分页信息
            currentPageResult = service.pageRoles(currentPageRequest, currentQuery);

            // 更新分页信息（如果组件已初始化）
            if (pageInfo != null && currentPageResult != null && currentPageResult.getData() != null) {
                updatePaginationInfo();
            } else if (pageInfo != null) {
                pageInfo.setText(I18NUtil.get("common.noData"));
                updatePaginationButtons(false, false);
            }
            
            // 监听选中项变化，显示/隐藏批量操作栏（如果组件已初始化）
            if (batchDeleteButton != null) {
                updateBatchOperationBar();
            }
            
        } catch (Exception e) {
            NotificationUtil.showError(I18NUtil.get("role.query.failed", e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * 构建查询条件
     */
    private void buildQuery() {
        currentQuery = new RoleQueryDTO();
        
        // 检查字段是否已初始化（防止在父类构造函数调用时出现空指针）
        if (nameSearchField != null) {
            String name = nameSearchField.getValue();
            if (name != null && !name.trim().isEmpty()) {
                currentQuery.setName(name.trim());
            }
        }
        
        if (codeSearchField != null) {
            String code = codeSearchField.getValue();
            if (code != null && !code.trim().isEmpty()) {
                currentQuery.setCode(code.trim());
            }
        }
        
        if (statusFilter != null) {
            String status = statusFilter.getValue();
            // 使用常量值进行比较，而不是 I18N 文本
            // I18N 只用于展示，不用于逻辑判断
            if (status != null && !StatusConstant.ALL.equals(status)) {
                if (StatusConstant.ENABLED.equals(status)) {
                    currentQuery.setIsEnabled(true);
                } else if (StatusConstant.DISABLED.equals(status)) {
                    currentQuery.setIsEnabled(false);
                }
            }
        }
    }

    /**
     * 重置搜索条件
     */
    private void resetSearch() {
        if (nameSearchField != null) {
            nameSearchField.clear();
        }
        if (codeSearchField != null) {
            codeSearchField.clear();
        }
        if (statusFilter != null) {
            statusFilter.setValue(StatusConstant.ALL);
        }
        currentPageRequest.setPageNum(1);
        performSearch();
    }

    /**
     * 更新批量操作栏显示状态
     */
    private void updateBatchOperationBar() {
        // 延迟添加选择监听器，确保Grid已初始化
        grid.addSelectionListener(e -> {
            Set<Role> selected = e.getAllSelectedItems();
            boolean hasSelection = !selected.isEmpty();
            
            // 查找批量操作栏并更新可见性
            getChildren()
                    .filter(component -> component instanceof HorizontalLayout)
                    .map(component -> (HorizontalLayout) component)
                    .filter(layout -> layout.getClassName().contains("batch-operation-bar"))
                    .findFirst()
                    .ifPresent(layout -> layout.setVisible(hasSelection));
        });
    }

    /**
     * 执行批量删除
     */
    private void performBatchDelete() {
        Set<Role> selected = grid.getSelectedItems();
        if (selected.isEmpty()) {
            NotificationUtil.showError(I18NUtil.get("common.selectAtLeastOne") + I18NUtil.get("role.title"));
            return;
        }

        List<Long> ids = selected.stream().map(Role::getId).collect(Collectors.toList());
        String names = selected.stream().map(Role::getName).collect(Collectors.joining("、"));

        ConfirmDialogUtil.createBatchDeleteDialog(
            I18NUtil.get("role.title"),
            ids.size(),
            names,
            () -> {
                try {
                    service.batchDeleteRoles(ids);
                    NotificationUtil.showSuccess(I18NUtil.get("role.batch.delete.success", ids.size()));
                    grid.deselectAll();
                    performSearch();
                } catch (Exception ex) {
                    NotificationUtil.showError(I18NUtil.get("role.batch.delete.failed", ex.getMessage()));
                }
            }
        ).open();
    }

    /**
     * 执行批量更新状态
     */
    private void performBatchUpdateStatus(boolean isEnabled) {
        Set<Role> selected = grid.getSelectedItems();
        if (selected.isEmpty()) {
            NotificationUtil.showError(I18NUtil.get("common.selectAtLeastOne") + I18NUtil.get("role.title"));
            return;
        }

        List<Long> ids = selected.stream().map(Role::getId).collect(Collectors.toList());
        String entityName = I18NUtil.get("role.title");
        String actionKey = isEnabled ? "role.batch.enable" : "role.batch.disable";

        (isEnabled ? 
            ConfirmDialogUtil.createBatchEnableDialog(entityName, ids.size(), () -> {
                try {
                    service.batchUpdateRoleStatus(ids, true);
                    NotificationUtil.showSuccess(I18NUtil.get(actionKey + ".success", ids.size()));
                    grid.deselectAll();
                    performSearch();
                } catch (Exception ex) {
                    NotificationUtil.showError(I18NUtil.get(actionKey + ".failed", ex.getMessage()));
                }
            }) :
            ConfirmDialogUtil.createBatchDisableDialog(entityName, ids.size(), () -> {
                try {
                    service.batchUpdateRoleStatus(ids, false);
                    NotificationUtil.showSuccess(I18NUtil.get(actionKey + ".success", ids.size()));
                    grid.deselectAll();
                    performSearch();
                } catch (Exception ex) {
                    NotificationUtil.showError(I18NUtil.get(actionKey + ".failed", ex.getMessage()));
                }
            })
        ).open();
    }

    /**
     * 更新分页信息
     */
    private void updatePaginationInfo() {
        if (currentPageResult != null && currentPageResult.getData() != null) {
            PageResult.PageData<Role> pageData = currentPageResult.getData();
            int pageNum = pageData.getPageNum();
            int totalPages = PaginationUtil.calculateTotalPages(pageData);

            pageInfo.setText(I18NUtil.get("pagination.info", pageNum, totalPages > 0 ? totalPages : 1, pageData.getTotal()));

            // 更新按钮状态
            updatePaginationButtons(
                PaginationUtil.hasPrevPage(pageData),
                PaginationUtil.hasNextPage(pageData)
            );
        }
    }

    /**
     * 更新分页按钮状态
     */
    private void updatePaginationButtons(boolean hasPrev, boolean hasNext) {
        firstPageButton.setEnabled(hasPrev);
        prevPageButton.setEnabled(hasPrev);
        nextPageButton.setEnabled(hasNext);
        lastPageButton.setEnabled(hasNext);
    }

    @Override
    protected void updateList() {
        // 重写此方法，使用分页查询
        performSearch();
    }

    @Override
    public String getPageTitle() {
        return I18NUtil.get("page.role.management");
    }
}
