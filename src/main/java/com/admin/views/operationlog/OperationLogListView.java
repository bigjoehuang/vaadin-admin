package com.admin.views.operationlog;

import com.admin.entity.OperationLog;
import com.admin.service.OperationLogService;
import com.admin.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * 操作日志列表视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "operation-logs", layout = MainLayout.class)
@PageTitle("操作日志")
public class OperationLogListView extends VerticalLayout {

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
        grid.addColumn(OperationLog::getUsername).setHeader("用户名").setWidth("120px").setFlexGrow(0);
        grid.addColumn(OperationLog::getOperation).setHeader("操作类型").setWidth("150px").setFlexGrow(0);
        grid.addColumn(OperationLog::getMethod).setHeader("请求方法").setWidth("100px").setFlexGrow(0);
        grid.addColumn(log -> {
            String params = log.getParams();
            if (params != null && params.length() > 50) {
                return params.substring(0, 50) + "...";
            }
            return params;
        }).setHeader("请求参数").setFlexGrow(1);
        grid.addColumn(OperationLog::getIp).setHeader("IP地址").setWidth("150px").setFlexGrow(0);
        grid.addColumn(log -> log.getStatus() == 1 ? "成功" : "失败").setHeader("状态").setWidth("80px").setFlexGrow(0);
        grid.addColumn(OperationLog::getErrorMsg).setHeader("错误信息").setFlexGrow(1);
        grid.addColumn(OperationLog::getCreatedAt).setHeader("操作时间").setWidth("180px").setFlexGrow(0);
        grid.getColumns().forEach(col -> col.setAutoWidth(false));
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassName("toolbar");
        toolbar.setSpacing(true);
        return toolbar;
    }

    private HorizontalLayout getSearchBar() {
        usernameSearchField = new TextField("用户名");
        usernameSearchField.setPlaceholder("请输入用户名");
        usernameSearchField.setWidth("200px");
        usernameSearchField.setClearButtonVisible(true);

        operationSearchField = new TextField("操作类型");
        operationSearchField.setPlaceholder("请输入操作类型");
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
            com.admin.util.NotificationUtil.showError("加载操作日志列表失败：" + e.getMessage());
        }
    }
}

