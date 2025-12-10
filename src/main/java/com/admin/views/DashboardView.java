package com.admin.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * 仪表盘视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("仪表盘")
public class DashboardView extends VerticalLayout implements AuthenticatedRoute {

    public DashboardView() {
        add(new H2("欢迎使用 Vaadin Admin"));
    }
}

