package com.admin.views.operationlog;

import com.admin.entity.OperationLog;
import com.admin.service.OperationLogService;
import com.admin.util.I18NUtil;
import com.admin.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * 操作日志列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "operation-logs", layout = MainLayout.class)
public class OperationLogListView extends VerticalLayout implements HasDynamicTitle {

    private final OperationLogService operationLogService;
    private final Grid<OperationLog> grid = new Grid<>(OperationLog.class, false);
    private TextField usernameSearchField;
    private TextField operationSearchField;

    public OperationLogListView(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
        addClassName("operation-log-list-view");
        setSizeFull();

        configureGrid();
        add(getToolbar(), getSearchBar(), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addColumn(OperationLog::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(OperationLog::getUsername).setHeader(I18NUtil.get("operation.log.username")).setWidth("120px").setFlexGrow(0);
        grid.addColumn(OperationLog::getOperation).setHeader(I18NUtil.get("operation.log.operation")).setWidth("150px").setFlexGrow(0);
        grid.addColumn(OperationLog::getMethod).setHeader(I18NUtil.get("operation.log.method")).setWidth("100px").setFlexGrow(0);
        grid.addColumn(log -> {
            String params = log.getParams();
            if (params != null && params.length() > 50) {
                return params.substring(0, 50) + "...";
            }
            return params;
        }).setHeader(I18NUtil.get("operation.log.params")).setFlexGrow(1);
        grid.addColumn(OperationLog::getIp).setHeader(I18NUtil.get("operation.log.ip")).setWidth("150px").setFlexGrow(0);
        grid.addColumn(log -> log.getStatus() == 1 ? I18NUtil.get("operation.log.success") : I18NUtil.get("operation.log.failed")).setHeader(I18NUtil.get("operation.log.status")).setWidth("80px").setFlexGrow(0);
        grid.addColumn(OperationLog::getErrorMsg).setHeader(I18NUtil.get("operation.log.errorMsg")).setFlexGrow(1);
        grid.addColumn(OperationLog::getCreatedAt).setHeader(I18NUtil.get("operation.log.createdAt")).setWidth("180px").setFlexGrow(0);
        grid.getColumns().forEach(col -> col.setAutoWidth(false));
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassName("toolbar");
        toolbar.setSpacing(true);
        return toolbar;
    }

    private HorizontalLayout getSearchBar() {
        usernameSearchField = new TextField(I18NUtil.get("operation.log.username"));
        usernameSearchField.setPlaceholder(I18NUtil.get("operation.log.placeholder.username"));
        usernameSearchField.setWidth("200px");
        usernameSearchField.setClearButtonVisible(true);

        operationSearchField = new TextField(I18NUtil.get("operation.log.operation"));
        operationSearchField.setPlaceholder(I18NUtil.get("operation.log.placeholder.operation"));
        operationSearchField.setWidth("200px");
        operationSearchField.setClearButtonVisible(true);

        HorizontalLayout searchBar = new HorizontalLayout(usernameSearchField, operationSearchField);
        searchBar.setSpacing(true);
        searchBar.setWidthFull();
        searchBar.addClassName("search-bar");
        return searchBar;
    }

    private void updateList() {
        try {
            List<OperationLog> logs = operationLogService.listLogs();
            // 简单的客户端过滤（实际应该在后端实现）
            String usernameFilter = usernameSearchField.getValue();
            String operationFilter = operationSearchField.getValue();
            
            List<OperationLog> filteredLogs = logs.stream()
                    .filter(log -> {
                        if (usernameFilter != null && !usernameFilter.isEmpty()) {
                            if (log.getUsername() == null || !log.getUsername().contains(usernameFilter)) {
                                return false;
                            }
                        }
                        if (operationFilter != null && !operationFilter.isEmpty()) {
                            if (log.getOperation() == null || !log.getOperation().contains(operationFilter)) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .toList();
            
            grid.setItems(filteredLogs);
        } catch (Exception e) {
            com.admin.util.NotificationUtil.showError(I18NUtil.get("operation.log.load.failed", e.getMessage()));
        }
    }

    @Override
    public String getPageTitle() {
        return I18NUtil.get("page.operation.log");
    }
}

