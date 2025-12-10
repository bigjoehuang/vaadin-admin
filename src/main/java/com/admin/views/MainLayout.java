package com.admin.views;

import com.admin.views.menu.MenuListView;
import com.admin.views.role.RoleListView;
import com.admin.views.user.UserListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.dependency.JsModule;

/**
 * 主布局
 *
 * @author Admin
 * @date 2024-01-01
 */
@JsModule("./themes/admin-theme/drawer-toggle-styles.js")
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private static final String LAST_ROUTE_KEY = "lastRoute";
    private Tabs tabs;

    public MainLayout() {
        createHeader();
        createDrawer();
        // 设置菜单栏宽度（通过 CSS 变量）
        getElement().getStyle().set("--drawer-width", "200px");
        getElement().getStyle().set("--vaadin-app-layout-drawer-width", "200px");
        
        // 恢复上次访问的路由
        restoreLastRoute();
    }

    private void createHeader() {
        H1 logo = new H1("Vaadin Admin");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.addClassNames("drawer-toggle-button");
        var header = new HorizontalLayout(drawerToggle, logo);

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
        tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(createTab("仪表盘", DashboardView.class));
        tabs.add(createTab("用户管理", UserListView.class));
        tabs.add(createTab("角色管理", RoleListView.class));
        tabs.add(createTab("菜单管理", MenuListView.class));
        return tabs;
    }

    private Tab createTab(String viewName, Class<? extends com.vaadin.flow.component.Component> viewClass) {
        RouterLink link = new RouterLink(viewName, viewClass);
        link.setTabIndex(-1);
        return new Tab(link);
    }

    /**
     * 恢复上次访问的路由
     */
    private void restoreLastRoute() {
        getUI().ifPresent(ui -> {
            // 延迟执行，确保页面已完全加载
            ui.getPage().executeJs(
                "setTimeout(function() {" +
                "  var lastRoute = sessionStorage.getItem('" + LAST_ROUTE_KEY + "');" +
                "  if (lastRoute && lastRoute !== '' && lastRoute !== '/' && window.location.pathname === '/') {" +
                "    window.history.pushState({}, '', lastRoute);" +
                "    window.dispatchEvent(new PopStateEvent('popstate', { state: {} }));" +
                "  }" +
                "}, 100);"
            );
        });
    }

    /**
     * 保存当前路由到SessionStorage
     */
    private void saveCurrentRoute(String route) {
        if (route != null && !route.isEmpty() && !route.equals("/login")) {
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs(
                    "sessionStorage.setItem($0, $1);", 
                    LAST_ROUTE_KEY, 
                    route
                );
            });
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // 导航后保存当前路由
        String currentRoute = event.getLocation().getPath();
        saveCurrentRoute(currentRoute);
        
        // 更新Tabs的选中状态
        if (tabs != null) {
            tabs.getChildren()
                .filter(Tab.class::isInstance)
                .map(Tab.class::cast)
                .forEach(tab -> {
                    RouterLink link = (RouterLink) tab.getChildren()
                        .filter(RouterLink.class::isInstance)
                        .findFirst()
                        .orElse(null);
                    if (link != null) {
                        String href = link.getHref();
                        if (href != null && (currentRoute.equals(href) || 
                            (currentRoute.isEmpty() && href.equals("")))) {
                            tabs.setSelectedTab(tab);
                        }
                    }
                });
        }
    }
}
