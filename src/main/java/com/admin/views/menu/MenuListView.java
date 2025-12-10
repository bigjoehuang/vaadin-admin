package com.admin.views.menu;

import com.admin.component.BaseFormDialog;
import com.admin.dto.MenuQueryDTO;
import com.admin.dto.PageRequest;
import com.admin.entity.Menu;
import com.admin.service.MenuService;
import com.admin.util.NotificationUtil;
import com.admin.util.PageResult;
import com.admin.views.MainLayout;
import com.admin.views.base.BaseListView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "menus", layout = MainLayout.class)
@PageTitle("菜单管理")
public class MenuListView extends BaseListView<Menu, MenuService> {

    // 搜索和筛选组件
    private TextField nameSearchField;
    private TextField pathSearchField;
    private ComboBox<String> statusFilter;
    private Button searchButton;
    private Button resetButton;

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
    private MenuQueryDTO currentQuery = new MenuQueryDTO();
    private PageRequest currentPageRequest = new PageRequest();

    // 当前分页数据
    private PageResult<Menu> currentPageResult;

    public MenuListView(MenuService menuService) {
        super(menuService, Menu.class, "菜单", "添加菜单", "menu-list-view");

        // 启用Grid多选模式
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

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
        grid.addColumn(menu -> "").setHeader("").setWidth("30px").setFlexGrow(0);

        grid.addColumn(Menu::getId).setHeader("ID").setWidth("80px").setFlexGrow(0).setSortable(true);
        grid.addColumn(Menu::getName).setHeader("菜单名称").setFlexGrow(1);
        grid.addColumn(Menu::getPath).setHeader("路径").setFlexGrow(1);
        grid.addColumn(Menu::getComponent).setHeader("组件").setFlexGrow(1);
        grid.addColumn(Menu::getIcon).setHeader("图标").setWidth("120px").setFlexGrow(0);
        grid.addColumn(Menu::getSort).setHeader("排序").setWidth("80px").setFlexGrow(0).setSortable(true);

        // 优化状态列显示（使用图标和颜色）
        grid.addComponentColumn(menu -> {
            boolean enabled = menu.getIsEnabled() != null && menu.getIsEnabled();

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
            Span textSpan = new Span(enabled ? "启用" : "禁用");
            textSpan.getStyle().set("font-size", "var(--lumo-font-size-s)");

            statusLayout.add(icon, textSpan);
            statusLayout.setFlexGrow(0, icon);
            statusLayout.setFlexGrow(1, textSpan);

            return statusLayout;
        }).setHeader("状态").setWidth("100px").setFlexGrow(0);

        grid.addColumn(Menu::getCreatedAt).setHeader("创建时间").setWidth("180px").setFlexGrow(0).setSortable(true);
        grid.addColumn(Menu::getUpdatedAt).setHeader("更新时间").setWidth("180px").setFlexGrow(0).setSortable(true);

        // 添加操作列
        grid.addComponentColumn(menu -> {
            HorizontalLayout actionLayout = new HorizontalLayout();
            actionLayout.setSpacing(true);

            Button editButton = new Button("编辑", new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> editEntity(menu));

            Button deleteButton = new Button("删除", new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> deleteEntity(menu));

            actionLayout.add(editButton, deleteButton);
            return actionLayout;
        }).setHeader("操作").setWidth("180px").setFlexGrow(0);
    }

    @Override
    protected List<Menu> getListData() {
        // 使用分页查询，这里返回空列表，实际数据通过分页组件加载
        if (currentPageResult != null && currentPageResult.getData() != null) {
            return currentPageResult.getData().getList();
        }
        return new ArrayList<>();
    }

    @Override
    protected BaseFormDialog<Menu> getFormDialog(boolean isEdit, Menu entity) {
        MenuFormDialog dialog = new MenuFormDialog(service, isEdit, this::performSearch);
        if (isEdit && entity != null) {
            dialog.setEntity(entity);
        }
        return dialog;
    }

    @Override
    protected void performDelete(Menu entity) {
        service.deleteMenu(entity.getId());
    }

    /**
     * 构建工具栏
     */
    private HorizontalLayout buildToolbar() {
        Button addButton = new Button("添加菜单", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> addEntity());

        Button refreshButton = new Button("刷新", new Icon(VaadinIcon.REFRESH));
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
        nameSearchField = new TextField("菜单名称");
        nameSearchField.setPlaceholder("请输入菜单名称");
        nameSearchField.setWidth("200px");
        nameSearchField.setClearButtonVisible(true);
        nameSearchField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        nameSearchField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        pathSearchField = new TextField("菜单路径");
        pathSearchField.setPlaceholder("请输入菜单路径");
        pathSearchField.setWidth("200px");
        pathSearchField.setClearButtonVisible(true);
        pathSearchField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        pathSearchField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        statusFilter = new ComboBox<>("状态");
        statusFilter.setItems("全部", "启用", "禁用");
        statusFilter.setValue("全部");
        statusFilter.setWidth("120px");
        statusFilter.setClearButtonVisible(true);
        statusFilter.setPlaceholder("选择状态");

        searchButton = new Button("搜索", new Icon(VaadinIcon.SEARCH));
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(e -> performSearch());

        resetButton = new Button("重置", new Icon(VaadinIcon.REFRESH));
        resetButton.addClickListener(e -> resetSearch());

        HorizontalLayout searchBar = new HorizontalLayout(
                nameSearchField, pathSearchField, statusFilter, searchButton, resetButton
        );
        searchBar.setSpacing(true);
        searchBar.setAlignItems(FlexComponent.Alignment.END);
        searchBar.setWidthFull();
        searchBar.addClassName("search-bar");

        return searchBar;
    }

    /**
     * 构建批量操作栏
     */
    private HorizontalLayout buildBatchOperationBar() {
        batchDeleteButton = new Button("批量删除", new Icon(VaadinIcon.TRASH));
        batchDeleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        batchDeleteButton.addClickListener(e -> performBatchDelete());

        batchEnableButton = new Button("批量启用", new Icon(VaadinIcon.CHECK));
        batchEnableButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        batchEnableButton.addClickListener(e -> performBatchUpdateStatus(true));

        batchDisableButton = new Button("批量禁用", new Icon(VaadinIcon.CLOSE));
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
        firstPageButton = new Button("首页", new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
        firstPageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        firstPageButton.addClickListener(e -> {
            currentPageRequest.setPageNum(1);
            performSearch();
        });

        prevPageButton = new Button("上一页", new Icon(VaadinIcon.ANGLE_LEFT));
        prevPageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        prevPageButton.addClickListener(e -> {
            if (currentPageRequest.getPageNum() > 1) {
                currentPageRequest.setPageNum(currentPageRequest.getPageNum() - 1);
                performSearch();
            }
        });

        nextPageButton = new Button("下一页", new Icon(VaadinIcon.ANGLE_RIGHT));
        nextPageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        nextPageButton.addClickListener(e -> {
            if (currentPageResult != null && currentPageResult.getData() != null) {
                long total = currentPageResult.getData().getTotal();
                int totalPages = (int) Math.ceil((double) total / currentPageRequest.getPageSize());
                if (currentPageRequest.getPageNum() < totalPages) {
                    currentPageRequest.setPageNum(currentPageRequest.getPageNum() + 1);
                    performSearch();
                }
            }
        });

        lastPageButton = new Button("末页", new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
        lastPageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        lastPageButton.addClickListener(e -> {
            if (currentPageResult != null && currentPageResult.getData() != null) {
                long total = currentPageResult.getData().getTotal();
                int totalPages = (int) Math.ceil((double) total / currentPageRequest.getPageSize());
                currentPageRequest.setPageNum(totalPages > 0 ? totalPages : 1);
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
            if (nameSearchField == null || pathSearchField == null || statusFilter == null) {
                // 如果字段未初始化，使用默认查询（查询所有数据）
                currentQuery = new MenuQueryDTO();
            } else {
                // 构建查询条件
                buildQuery();
            }

            // 确保查询条件不为null
            if (currentQuery == null) {
                currentQuery = new MenuQueryDTO();
            }

            // 执行分页查询
            currentPageResult = service.pageMenus(currentPageRequest, currentQuery);

            // 更新Grid数据
            if (currentPageResult != null && currentPageResult.getData() != null) {
                List<Menu> menus = currentPageResult.getData().getList();
                grid.setItems(menus);

                // 更新分页信息（如果组件已初始化）
                if (pageInfo != null) {
                    updatePaginationInfo();
                }
            } else {
                grid.setItems(new ArrayList<>());
                if (pageInfo != null) {
                    pageInfo.setText("暂无数据");
                    updatePaginationButtons(false, false);
                }
            }

            // 监听选中项变化，显示/隐藏批量操作栏（如果组件已初始化）
            if (batchDeleteButton != null) {
                updateBatchOperationBar();
            }

        } catch (Exception e) {
            NotificationUtil.showError("查询失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 构建查询条件
     */
    private void buildQuery() {
        currentQuery = new MenuQueryDTO();

        // 检查字段是否已初始化（防止在父类构造函数调用时出现空指针）
        if (nameSearchField != null) {
            String name = nameSearchField.getValue();
            if (name != null && !name.trim().isEmpty()) {
                currentQuery.setName(name.trim());
            }
        }

        if (pathSearchField != null) {
            String path = pathSearchField.getValue();
            if (path != null && !path.trim().isEmpty()) {
                currentQuery.setPath(path.trim());
            }
        }

        if (statusFilter != null) {
            String status = statusFilter.getValue();
            // 如果清除选择（值为null或空），或者选择"全部"，则不设置状态筛选条件
            if (status != null && !status.trim().isEmpty() && !"全部".equals(status)) {
                currentQuery.setIsEnabled("启用".equals(status));
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
        if (pathSearchField != null) {
            pathSearchField.clear();
        }
        if (statusFilter != null) {
            statusFilter.setValue("全部");
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
            Set<Menu> selected = e.getAllSelectedItems();
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
        Set<Menu> selected = grid.getSelectedItems();
        if (selected.isEmpty()) {
            NotificationUtil.showError("请至少选择一个菜单");
            return;
        }

        List<Long> ids = selected.stream().map(Menu::getId).collect(Collectors.toList());
        String names = selected.stream().map(Menu::getName).collect(Collectors.joining("、"));

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("确认批量删除");
        confirmDialog.setText("确定要删除以下 " + ids.size() + " 个菜单吗？\n" + names + "\n此操作不可恢复。");
        confirmDialog.setConfirmText("删除");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.setCancelText("取消");
        confirmDialog.setCancelButtonTheme("tertiary");
        confirmDialog.setCancelable(true);

        confirmDialog.addConfirmListener(e -> {
            try {
                service.batchDeleteMenus(ids);
                NotificationUtil.showSuccess("批量删除成功，共删除 " + ids.size() + " 个菜单");
                grid.deselectAll();
                performSearch();
            } catch (Exception ex) {
                NotificationUtil.showError("批量删除失败：" + ex.getMessage());
            }
        });

        confirmDialog.addCancelListener(e -> {
            // 用户点击取消，关闭对话框
        });

        confirmDialog.open();
    }

    /**
     * 执行批量更新状态
     */
    private void performBatchUpdateStatus(boolean isEnabled) {
        Set<Menu> selected = grid.getSelectedItems();
        if (selected.isEmpty()) {
            NotificationUtil.showError("请至少选择一个菜单");
            return;
        }

        List<Long> ids = selected.stream().map(Menu::getId).collect(Collectors.toList());
        String action = isEnabled ? "启用" : "禁用";

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("确认批量" + action);
        confirmDialog.setText("确定要" + action + "以下 " + ids.size() + " 个菜单吗？");
        confirmDialog.setConfirmText(action);
        confirmDialog.setConfirmButtonTheme("primary");
        confirmDialog.setCancelText("取消");
        confirmDialog.setCancelButtonTheme("tertiary");
        confirmDialog.setCancelable(true);

        confirmDialog.addConfirmListener(e -> {
            try {
                service.batchUpdateMenuStatus(ids, isEnabled);
                NotificationUtil.showSuccess("批量" + action + "成功，共" + action + " " + ids.size() + " 个菜单");
                grid.deselectAll();
                performSearch();
            } catch (Exception ex) {
                NotificationUtil.showError("批量" + action + "失败：" + ex.getMessage());
            }
        });

        confirmDialog.addCancelListener(e -> {
            // 用户点击取消，关闭对话框
        });

        confirmDialog.open();
    }

    /**
     * 更新分页信息
     */
    private void updatePaginationInfo() {
        if (currentPageResult != null && currentPageResult.getData() != null) {
            long total = currentPageResult.getData().getTotal();
            int pageNum = currentPageResult.getData().getPageNum();
            int pageSize = currentPageResult.getData().getPageSize();
            int totalPages = (int) Math.ceil((double) total / pageSize);

            pageInfo.setText(String.format("第 %d/%d 页，共 %d 条记录", pageNum, totalPages > 0 ? totalPages : 1, total));

            // 更新按钮状态
            updatePaginationButtons(pageNum > 1, pageNum < totalPages);
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
}
