package com.admin.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.Theme;

/**
 * 主布局
 *
 * @author Admin
 * @date 2024-01-01
 */
@Theme("admin-theme")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Vaadin Admin");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        var header = new HorizontalLayout(new DrawerToggle(), logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        addToDrawer(new VerticalLayout(
                createNavigation()
        ));
    }

    private Tabs createNavigation() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(createTab("仪表盘", DashboardView.class));
        tabs.add(createTab("用户管理", UserListView.class));
        tabs.add(createTab("角色管理", RoleListView.class));
        tabs.add(createTab("菜单管理", MenuListView.class));
        return tabs;
    }

    private Tab createTab(String viewName, Class<?> viewClass) {
        RouterLink link = new RouterLink();
        link.add(viewName);
        link.setRoute(viewClass);
        link.setTabIndex(-1);

        return new Tab(link);
    }
}
