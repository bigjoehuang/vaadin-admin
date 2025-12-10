package com.admin.views;

import com.admin.service.UserService;
import com.admin.util.ThemeUtil;
import com.admin.util.UserUtil;
import com.admin.views.menu.MenuListView;
import com.admin.views.role.RoleListView;
import com.admin.views.user.UserListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
@JsModule("./themes/admin-theme/theme-toggle.js")
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private static final String LAST_ROUTE_KEY = "lastRoute";
    private Tabs tabs;
    private final UserService userService;

    public MainLayout(UserService userService) {
        this.userService = userService;
        createHeader();
        createDrawer();
        // 设置菜单栏宽度（通过 CSS 变量）
        getElement().getStyle().set("--drawer-width", "200px");
        getElement().getStyle().set("--vaadin-app-layout-drawer-width", "200px");
        
        // 初始化主题（从 localStorage 恢复）
        ThemeUtil.initTheme();
        
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
        
        // 创建主题切换按钮
        Component themeToggleButton = createThemeToggleButton();
        
        // 创建用户信息区域（右上角）
        Component userMenu = createUserMenu();
        
        // 创建右上角操作区域（主题切换按钮 + 用户菜单）
        HorizontalLayout rightActions = new HorizontalLayout(themeToggleButton, userMenu);
        rightActions.setSpacing(true);
        rightActions.setAlignItems(FlexComponent.Alignment.CENTER);
        
        var header = new HorizontalLayout(drawerToggle, logo, rightActions);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setFlexGrow(1, logo);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    /**
     * 创建主题切换按钮
     */
    private Component createThemeToggleButton() {
        Button themeButton = new Button();
        Icon moonIcon = new Icon(VaadinIcon.MOON_O);
        moonIcon.getStyle().set("color", "var(--lumo-body-text-color)");
        themeButton.setIcon(moonIcon);
        themeButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
        themeButton.setAriaLabel("切换主题");
        themeButton.addClassName("theme-toggle-button");
        
        // 更新图标的方法
        Runnable updateIcon = () -> {
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs(
                    "return window.getCurrentTheme ? window.getCurrentTheme() : 'light';"
                ).then(String.class, theme -> {
                    Icon icon = "dark".equals(theme) 
                        ? new Icon(VaadinIcon.SUN_O) 
                        : new Icon(VaadinIcon.MOON_O);
                    icon.getStyle().set("color", "var(--lumo-body-text-color)");
                    themeButton.setIcon(icon);
                });
            });
        };
        
        themeButton.addClickListener(e -> {
            // 先获取当前主题
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs(
                    "return window.getCurrentTheme ? window.getCurrentTheme() : 'light';"
                ).then(String.class, currentTheme -> {
                    // 切换主题
                    ThemeUtil.toggleTheme();
                    
                    // 根据切换后的主题更新图标（切换是确定的：light -> dark, dark -> light）
                    String newTheme = "dark".equals(currentTheme) ? "light" : "dark";
                    Icon icon = "dark".equals(newTheme) 
                        ? new Icon(VaadinIcon.SUN_O) 
                        : new Icon(VaadinIcon.MOON_O);
                    icon.getStyle().set("color", "var(--lumo-body-text-color)");
                    themeButton.setIcon(icon);
                });
            });
        });
        
        // 初始化按钮图标
        updateIcon.run();
        
        return themeButton;
    }

    /**
     * 创建用户菜单（右上角）
     */
    private Component createUserMenu() {
        // 获取当前用户信息
        String userName = UserUtil.getCurrentUserName();
        com.admin.entity.User currentUser = UserUtil.getCurrentUser();
        
        // 创建用户信息容器
        HorizontalLayout userLayout = new HorizontalLayout();
        userLayout.setSpacing(true);
        userLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        userLayout.addClassName("user-menu-layout");
        
        // 创建头像
        Avatar avatar = new Avatar();
        if (currentUser != null && currentUser.getNickname() != null && !currentUser.getNickname().isEmpty()) {
            avatar.setName(currentUser.getNickname());
        } else if (userName != null) {
            avatar.setName(userName);
        } else {
            avatar.setName("用户");
        }
        avatar.setImage(currentUser != null ? currentUser.getAvatar() : null);
        
        // 创建用户名显示
        Span userNameSpan = new Span();
        if (currentUser != null && currentUser.getNickname() != null && !currentUser.getNickname().isEmpty()) {
            userNameSpan.setText(currentUser.getNickname());
        } else if (userName != null) {
            userNameSpan.setText(userName);
        } else {
            userNameSpan.setText("用户");
        }
        userNameSpan.getStyle().set("font-size", "var(--lumo-font-size-m)");
        userNameSpan.getStyle().set("color", "var(--lumo-body-text-color)");
        
        // 创建下拉图标
        Icon dropdownIcon = new Icon(VaadinIcon.CHEVRON_DOWN);
        dropdownIcon.setSize("16px");
        dropdownIcon.getStyle().set("color", "var(--lumo-body-text-color)");
        
        userLayout.add(avatar, userNameSpan, dropdownIcon);
        userLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        
        // 创建上下文菜单
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(userLayout);
        contextMenu.setOpenOnClick(true);
        
        // 添加"修改密码"菜单项
        MenuItem changePasswordItem = contextMenu.addItem("修改密码", e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(userService);
            dialog.open();
        });
        changePasswordItem.addComponentAsFirst(new Icon(VaadinIcon.KEY));
        
        // 添加"退出登录"菜单项
        MenuItem logoutItem = contextMenu.addItem("退出登录", e -> {
            getUI().ifPresent(ui -> {
                ui.getPage().setLocation("/logout");
            });
        });
        logoutItem.addComponentAsFirst(new Icon(VaadinIcon.SIGN_OUT));
        
        return userLayout;
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
        tabs.add(createTab("操作日志", com.admin.views.operationlog.OperationLogListView.class));
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
                "var lastRoute = sessionStorage.getItem($0);" +
                "var currentPath = window.location.pathname;" +
                "if (lastRoute && lastRoute !== '' && lastRoute !== '/' && " +
                "    (currentPath === '' || currentPath === '/')) {" +
                "  setTimeout(function() { window.location.href = lastRoute; }, 50);" +
                "}"
                , LAST_ROUTE_KEY
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
