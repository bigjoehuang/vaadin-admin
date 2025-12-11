package com.admin.views.user;

import com.admin.component.BaseFormDialog;
import com.admin.dto.PageRequest;
import com.admin.dto.UserQueryDTO;
import com.admin.entity.User;
import com.admin.service.RoleService;
import com.admin.service.UserService;
import com.admin.util.I18NUtil;
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
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "users", layout = MainLayout.class)
public class UserListView extends BaseListView<User, UserService> implements HasDynamicTitle {

    // 搜索和筛选组件
    private TextField userNameSearchField;
    private TextField nicknameSearchField;
    private TextField emailSearchField;
    private TextField phoneSearchField;
    private ComboBox<String> statusFilter;
    private Button searchButton;
    private Button resetButton;

    // 缓存的 i18n 值（Bug 2 修复）
    private String i18nAll;
    private String i18nEnabled;
    private String i18nDisabled;

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
    private UserQueryDTO currentQuery = new UserQueryDTO();
    private PageRequest currentPageRequest = new PageRequest();

    // 当前分页数据
    private PageResult<User> currentPageResult;

    private final RoleService roleService;

    public UserListView(UserService userService, RoleService roleService) {
        super(userService, User.class, I18NUtil.get("user.title"), I18NUtil.get("user.add"), "user-list-view");
        this.roleService = roleService;

        // 缓存 i18n 值（Bug 2 修复：避免每次搜索时重复调用）
        i18nAll = I18NUtil.get("common.all");
        i18nEnabled = I18NUtil.get("user.enabled");
        i18nDisabled = I18NUtil.get("user.disabled");

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
        grid.addColumn(user -> "").setHeader("").setWidth("30px").setFlexGrow(0);

        grid.addColumn(User::getId).setHeader("ID").setWidth("80px").setFlexGrow(0).setSortable(true);
        grid.addColumn(User::getUserName).setHeader(I18NUtil.get("user.userName")).setFlexGrow(1);
        grid.addColumn(User::getNickname).setHeader(I18NUtil.get("user.nickname")).setFlexGrow(1);
        grid.addColumn(User::getEmail).setHeader(I18NUtil.get("user.email")).setFlexGrow(1);
        grid.addColumn(User::getPhone).setHeader(I18NUtil.get("user.phone")).setFlexGrow(1);

        // 优化状态列显示（使用图标和颜色）
        grid.addComponentColumn(user -> {
            boolean enabled = user.getIsEnabled() != null && user.getIsEnabled();

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
            Span textSpan = new Span(enabled ? I18NUtil.get("user.enabled") : I18NUtil.get("user.disabled"));
            textSpan.getStyle().set("font-size", "var(--lumo-font-size-s)");

            statusLayout.add(icon, textSpan);
            statusLayout.setFlexGrow(0, icon);
            statusLayout.setFlexGrow(1, textSpan);

            return statusLayout;
        }).setHeader(I18NUtil.get("user.status")).setWidth("100px").setFlexGrow(0);

        grid.addColumn(User::getCreatedAt).setHeader(I18NUtil.get("user.createdAt")).setWidth("180px").setFlexGrow(0).setSortable(true);
        grid.addColumn(User::getUpdatedAt).setHeader(I18NUtil.get("user.updatedAt")).setWidth("180px").setFlexGrow(0).setSortable(true);

        // 添加操作列
        grid.addComponentColumn(user -> {
            HorizontalLayout actionLayout = new HorizontalLayout();
            actionLayout.setSpacing(true);

            Button editButton = new Button(I18NUtil.get("common.edit"), new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> editEntity(user));

            Button deleteButton = new Button(I18NUtil.get("common.delete"), new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> deleteEntity(user));

            Button assignRoleButton = new Button(I18NUtil.get("user.assign.role"), new Icon(VaadinIcon.USER_CHECK));
            assignRoleButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
            assignRoleButton.addClickListener(e -> {
                UserRoleAssignDialog dialog = new UserRoleAssignDialog(
                        service,
                        roleService,
                        user,
                        this::performSearch
                );
                dialog.open();
            });

            actionLayout.add(editButton, deleteButton, assignRoleButton);
            return actionLayout;
        }).setHeader(I18NUtil.get("user.operation")).setWidth("280px").setFlexGrow(0);
    }

    @Override
    protected List<User> getListData() {
        // 使用分页查询，这里返回空列表，实际数据通过分页组件加载
        if (currentPageResult != null && currentPageResult.getData() != null) {
            return currentPageResult.getData().getList();
        }
        return new ArrayList<>();
    }

    @Override
    protected BaseFormDialog<User> getFormDialog(boolean isEdit, User entity) {
        UserFormDialog dialog = new UserFormDialog(service, isEdit, this::performSearch);
        if (isEdit && entity != null) {
            dialog.setEntity(entity);
        }
        return dialog;
    }

    @Override
    protected void performDelete(User entity) {
        service.deleteUser(entity.getId());
    }

    @Override
    protected String getEntityDisplayName(User entity) {
        if (entity == null) {
            return "";
        }
        return entity.getUserName() != null ? entity.getUserName() : "";
    }

    /**
     * 构建工具栏
     */
    private HorizontalLayout buildToolbar() {
        Button addButton = new Button(I18NUtil.get("user.add"), new Icon(VaadinIcon.PLUS));
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
        userNameSearchField = new TextField(I18NUtil.get("user.userName"));
        userNameSearchField.setPlaceholder(I18NUtil.get("user.placeholder.userName"));
        userNameSearchField.setWidth("200px");
        userNameSearchField.setClearButtonVisible(true);
        userNameSearchField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        userNameSearchField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        nicknameSearchField = new TextField(I18NUtil.get("user.nickname"));
        nicknameSearchField.setPlaceholder(I18NUtil.get("user.placeholder.nickname"));
        nicknameSearchField.setWidth("200px");
        nicknameSearchField.setClearButtonVisible(true);
        nicknameSearchField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        nicknameSearchField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        emailSearchField = new TextField(I18NUtil.get("user.email"));
        emailSearchField.setPlaceholder(I18NUtil.get("user.placeholder.email"));
        emailSearchField.setWidth("200px");
        emailSearchField.setClearButtonVisible(true);
        emailSearchField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        emailSearchField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        phoneSearchField = new TextField(I18NUtil.get("user.phone"));
        phoneSearchField.setPlaceholder(I18NUtil.get("user.placeholder.phone"));
        phoneSearchField.setWidth("200px");
        phoneSearchField.setClearButtonVisible(true);
        phoneSearchField.getElement().getStyle().set("--lumo-text-field-label-color", "var(--lumo-body-text-color)");
        phoneSearchField.getElement().getStyle().set("--vaadin-input-field-label-color", "var(--lumo-body-text-color)");

        statusFilter = new ComboBox<>(I18NUtil.get("user.status"));
        statusFilter.setItems(i18nAll, i18nEnabled, i18nDisabled);
        statusFilter.setValue(i18nAll);
        statusFilter.setWidth("120px");
        statusFilter.setClearButtonVisible(true);
        statusFilter.setPlaceholder(I18NUtil.get("user.placeholder.status"));

        searchButton = new Button(I18NUtil.get("common.search"), new Icon(VaadinIcon.SEARCH));
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(e -> performSearch());

        resetButton = new Button(I18NUtil.get("common.reset"), new Icon(VaadinIcon.REFRESH));
        resetButton.addClickListener(e -> resetSearch());

        HorizontalLayout searchBar = new HorizontalLayout(
                userNameSearchField, nicknameSearchField, emailSearchField, phoneSearchField, statusFilter, searchButton, resetButton
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
        batchDeleteButton = new Button(I18NUtil.get("user.batch.delete"), new Icon(VaadinIcon.TRASH));
        batchDeleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        batchDeleteButton.addClickListener(e -> performBatchDelete());

        batchEnableButton = new Button(I18NUtil.get("user.batch.enable"), new Icon(VaadinIcon.CHECK));
        batchEnableButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        batchEnableButton.addClickListener(e -> performBatchUpdateStatus(true));

        batchDisableButton = new Button(I18NUtil.get("user.batch.disable"), new Icon(VaadinIcon.CLOSE));
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
                long total = currentPageResult.getData().getTotal();
                int totalPages = (int) Math.ceil((double) total / currentPageRequest.getPageSize());
                if (currentPageRequest.getPageNum() < totalPages) {
                    currentPageRequest.setPageNum(currentPageRequest.getPageNum() + 1);
                    performSearch();
                }
            }
        });

        lastPageButton = new Button(I18NUtil.get("common.lastPage"), new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
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
            if (userNameSearchField == null || nicknameSearchField == null || emailSearchField == null
                    || phoneSearchField == null || statusFilter == null) {
                // 如果字段未初始化，使用默认查询（查询所有数据）
                currentQuery = new UserQueryDTO();
            } else {
                // 构建查询条件
                buildQuery();
            }

            // 确保查询条件不为null
            if (currentQuery == null) {
                currentQuery = new UserQueryDTO();
            }

            // 执行分页查询
            currentPageResult = service.pageUsers(currentPageRequest, currentQuery);

            // 更新Grid数据
            if (currentPageResult != null && currentPageResult.getData() != null) {
                List<User> users = currentPageResult.getData().getList();
                grid.setItems(users);

                // 更新分页信息（如果组件已初始化）
                if (pageInfo != null) {
                    updatePaginationInfo();
                }
            } else {
                grid.setItems(new ArrayList<>());
                if (pageInfo != null) {
                    pageInfo.setText(I18NUtil.get("common.noData"));
                    updatePaginationButtons(false, false);
                }
            }

            // 监听选中项变化，显示/隐藏批量操作栏（如果组件已初始化）
            if (batchDeleteButton != null) {
                updateBatchOperationBar();
            }

        } catch (Exception e) {
            NotificationUtil.showError(I18NUtil.get("user.query.failed", e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * 构建查询条件
     */
    private void buildQuery() {
        currentQuery = new UserQueryDTO();

        // 检查字段是否已初始化（防止在父类构造函数调用时出现空指针）
        if (userNameSearchField != null) {
            String userName = userNameSearchField.getValue();
            if (userName != null && !userName.trim().isEmpty()) {
                currentQuery.setUserName(userName.trim());
            }
        }

        if (nicknameSearchField != null) {
            String nickname = nicknameSearchField.getValue();
            if (nickname != null && !nickname.trim().isEmpty()) {
                currentQuery.setNickname(nickname.trim());
            }
        }

        if (emailSearchField != null) {
            String email = emailSearchField.getValue();
            if (email != null && !email.trim().isEmpty()) {
                currentQuery.setEmail(email.trim());
            }
        }

        if (phoneSearchField != null) {
            String phone = phoneSearchField.getValue();
            if (phone != null && !phone.trim().isEmpty()) {
                currentQuery.setPhone(phone.trim());
            }
        }

        if (statusFilter != null) {
            String status = statusFilter.getValue();
            // #region agent log
            try {
                java.io.FileWriter fw = new java.io.FileWriter("/Users/hjoe/ai-creation/vaadin-admin/.cursor/debug.log", true);
                fw.write(String.format("{\"id\":\"log_%d_user_build\",\"timestamp\":%d,\"location\":\"UserListView.java:455\",\"message\":\"buildQuery called\",\"data\":{\"status\":\"%s\",\"i18nAll\":\"%s\",\"i18nEnabled\":\"%s\",\"i18nDisabled\":\"%s\"},\"sessionId\":\"debug-session\",\"runId\":\"post-fix\",\"hypothesisId\":\"H4\"}\n", System.currentTimeMillis(), System.currentTimeMillis(), status != null ? status : "null", i18nAll, i18nEnabled, i18nDisabled));
                fw.close();
            } catch (Exception e) {}
            // #endregion
            // 如果清除选择（值为null或空），或者选择"全部"，则不设置状态筛选条件
            // Bug 2 修复：使用缓存的 i18n 值，而不是每次调用 I18NUtil.get()
            if (status != null && !status.trim().isEmpty() && !i18nAll.equals(status)) {
                currentQuery.setIsEnabled(i18nEnabled.equals(status));
            }
        }
    }

    /**
     * 重置搜索条件
     */
    private void resetSearch() {
        if (userNameSearchField != null) {
            userNameSearchField.clear();
        }
        if (nicknameSearchField != null) {
            nicknameSearchField.clear();
        }
        if (emailSearchField != null) {
            emailSearchField.clear();
        }
        if (phoneSearchField != null) {
            phoneSearchField.clear();
        }
        if (statusFilter != null) {
            statusFilter.setValue(i18nAll);
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
            Set<User> selected = e.getAllSelectedItems();
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
        Set<User> selected = grid.getSelectedItems();
        if (selected.isEmpty()) {
            NotificationUtil.showError(I18NUtil.get("common.selectAtLeastOne") + I18NUtil.get("user.title"));
            return;
        }

        List<Long> ids = selected.stream().map(User::getId).collect(Collectors.toList());
        String names = selected.stream().map(User::getUserName).collect(Collectors.joining("、"));

        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader(I18NUtil.get("confirm.batch.delete.title"));
        confirmDialog.setText(I18NUtil.get("user.batch.delete.confirm", ids.size(), names));
        confirmDialog.setConfirmText(I18NUtil.get("common.delete"));
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.setCancelText(I18NUtil.get("common.cancel"));
        confirmDialog.setCancelButtonTheme("tertiary");
        confirmDialog.setCancelable(true);

        confirmDialog.addConfirmListener(e -> {
            try {
                service.batchDeleteUsers(ids);
                NotificationUtil.showSuccess(I18NUtil.get("user.batch.delete.success", ids.size()));
                grid.deselectAll();
                performSearch();
            } catch (Exception ex) {
                NotificationUtil.showError(I18NUtil.get("user.batch.delete.failed", ex.getMessage()));
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
        Set<User> selected = grid.getSelectedItems();
        if (selected.isEmpty()) {
            NotificationUtil.showError(I18NUtil.get("common.selectAtLeastOne") + I18NUtil.get("user.title"));
            return;
        }

        List<Long> ids = selected.stream().map(User::getId).collect(Collectors.toList());
        String actionKey = isEnabled ? "user.batch.enable" : "user.batch.disable";
        String action = isEnabled ? I18NUtil.get("user.enabled") : I18NUtil.get("user.disabled");

        ConfirmDialog confirmDialog = new ConfirmDialog();
        String confirmKey = isEnabled ? "confirm.batch.enable" : "confirm.batch.disable";
        confirmDialog.setHeader(I18NUtil.get(confirmKey + ".title"));
        confirmDialog.setText(I18NUtil.get(confirmKey + ".text", ids.size(), I18NUtil.get("user.title")));
        confirmDialog.setConfirmText(action);
        confirmDialog.setConfirmButtonTheme("primary");
        confirmDialog.setCancelText(I18NUtil.get("common.cancel"));
        confirmDialog.setCancelButtonTheme("tertiary");
        confirmDialog.setCancelable(true);

        confirmDialog.addConfirmListener(e -> {
            try {
                service.batchUpdateUserStatus(ids, isEnabled);
                NotificationUtil.showSuccess(I18NUtil.get(actionKey + ".success", ids.size()));
                grid.deselectAll();
                performSearch();
            } catch (Exception ex) {
                NotificationUtil.showError(I18NUtil.get(actionKey + ".failed", ex.getMessage()));
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

            pageInfo.setText(I18NUtil.get("pagination.info", pageNum, totalPages > 0 ? totalPages : 1, total));

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

    @Override
    public String getPageTitle() {
        return I18NUtil.get("page.user.management");
    }
}
