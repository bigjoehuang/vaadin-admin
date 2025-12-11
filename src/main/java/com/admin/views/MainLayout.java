package com.admin.views;

import com.admin.entity.Menu;
import com.admin.service.MenuService;
import com.admin.service.UserService;
import com.admin.util.I18NUtil;
import com.admin.util.LocaleUtil;
import com.admin.util.ThemeUtil;
import com.admin.util.UIRefreshUtil;
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

import java.util.List;
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
    private final MenuService menuService;

    public MainLayout(UserService userService, MenuService menuService) {
        this.userService = userService;
        this.menuService = menuService;
        createHeader();
        createDrawer();
        // 设置菜单栏宽度（通过 CSS 变量）
        getElement().getStyle().set("--drawer-width", "200px");
        getElement().getStyle().set("--vaadin-app-layout-drawer-width", "200px");
        
        // 初始化语言（从 localStorage 恢复）
        LocaleUtil.initLocale();
        
        // 初始化主题（从 localStorage 恢复）
        ThemeUtil.initTheme();
        
        // 恢复上次访问的路由
        restoreLastRoute();
    }

    private void createHeader() {
        H1 logo = new H1(I18NUtil.get("main.layout.app.name"));
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.addClassNames("drawer-toggle-button");
        
        // 创建语言切换按钮
        Component localeToggleButton = createLocaleToggleButton();
        
        // 创建主题切换按钮
        Component themeToggleButton = createThemeToggleButton();
        
        // 创建用户信息区域（右上角）
        Component userMenu = createUserMenu();
        
        // 创建右上角操作区域（语言切换 + 主题切换按钮 + 用户菜单）
        HorizontalLayout rightActions = new HorizontalLayout(localeToggleButton, themeToggleButton, userMenu);
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
     * 创建语言切换按钮
     */
    private Component createLocaleToggleButton() {
        Button localeButton = new Button();
        Icon globeIcon = new Icon(VaadinIcon.GLOBE);
        globeIcon.getStyle().set("color", "var(--lumo-body-text-color)");
        localeButton.setIcon(globeIcon);
        localeButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
        localeButton.setAriaLabel(I18NUtil.get("locale.switch"));
        localeButton.addClickListener(e -> {
            LocaleUtil.toggleLocale();
            // 刷新 UI 文本，无需刷新页面
            refreshUIText();
        });
        localeButton.addClassName("locale-toggle-button");
        return localeButton;
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
        themeButton.setAriaLabel(I18NUtil.get("main.layout.theme.toggle"));
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
            avatar.setName(I18NUtil.get("main.layout.user"));
        }
        avatar.setImage(currentUser != null ? currentUser.getAvatar() : null);
        
        // 创建用户名显示
        Span userNameSpan = new Span();
        if (currentUser != null && currentUser.getNickname() != null && !currentUser.getNickname().isEmpty()) {
            userNameSpan.setText(currentUser.getNickname());
        } else if (userName != null) {
            userNameSpan.setText(userName);
        } else {
            userNameSpan.setText(I18NUtil.get("main.layout.user"));
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
        MenuItem changePasswordItem = contextMenu.addItem(I18NUtil.get("main.layout.change.password"), e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(userService);
            dialog.open();
        });
        changePasswordItem.addComponentAsFirst(new Icon(VaadinIcon.KEY));
        
        // 添加"退出登录"菜单项
        MenuItem logoutItem = contextMenu.addItem(I18NUtil.get("main.layout.logout"), e -> {
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
        
        // 从数据库动态加载菜单
        try {
            Long userId = UserUtil.getCurrentUserId();
            if (userId != null) {
                List<Menu> menuTree = menuService.getMenuTreeByUserId(userId);
                for (Menu menu : menuTree) {
                    Tab tab = createTabFromMenu(menu);
                    if (tab != null) {
                        tabs.add(tab);
                    }
                }
            } else {
                // 如果无法获取用户ID，使用默认菜单（向后兼容）
                tabs.add(createTab(I18NUtil.get("main.layout.dashboard"), DashboardView.class));
                tabs.add(createTab(I18NUtil.get("main.layout.user.management"), UserListView.class));
                tabs.add(createTab(I18NUtil.get("main.layout.role.management"), RoleListView.class));
                tabs.add(createTab(I18NUtil.get("main.layout.menu.management"), MenuListView.class));
                tabs.add(createTab(I18NUtil.get("main.layout.operation.log"), com.admin.views.operationlog.OperationLogListView.class));
            }
        } catch (Exception e) {
            // 如果加载菜单失败，使用默认菜单（向后兼容）
            tabs.add(createTab(I18NUtil.get("main.layout.dashboard"), DashboardView.class));
            tabs.add(createTab(I18NUtil.get("main.layout.user.management"), UserListView.class));
            tabs.add(createTab(I18NUtil.get("main.layout.role.management"), RoleListView.class));
            tabs.add(createTab(I18NUtil.get("main.layout.menu.management"), MenuListView.class));
            tabs.add(createTab(I18NUtil.get("main.layout.operation.log"), com.admin.views.operationlog.OperationLogListView.class));
        }
        
        return tabs;
    }

    /**
     * 从菜单创建 Tab
     */
    private Tab createTabFromMenu(Menu menu) {
        if (menu == null || menu.getPath() == null || menu.getPath().isEmpty()) {
            return null;
        }
        
        // 根据 path 查找对应的 View 类
        Class<? extends com.vaadin.flow.component.Component> viewClass = getViewClassByPath(menu.getPath());
        if (viewClass == null) {
            return null;
        }
        
        String menuName = menu.getName() != null ? menu.getName() : menu.getPath();
        RouterLink link = new RouterLink(menuName, viewClass);
        link.setTabIndex(-1);
        
        // 如果有图标，添加图标
        if (menu.getIcon() != null && !menu.getIcon().isEmpty()) {
            try {
                VaadinIcon icon = VaadinIcon.valueOf(menu.getIcon().toUpperCase().replace(":", "_"));
                link.addComponentAsFirst(new Icon(icon));
            } catch (Exception e) {
                // 如果图标解析失败，忽略
            }
        }
        
        return new Tab(link);
    }
    
    /**
     * 根据 path 获取 View 类
     * 这里使用硬编码映射，后续可以扩展为从配置或注解读取
     */
    private Class<? extends com.vaadin.flow.component.Component> getViewClassByPath(String path) {
        // 移除前导斜杠
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        // Path 到 View 类的映射
        switch (path) {
            case "":
            case "dashboard":
                return DashboardView.class;
            case "users":
                return UserListView.class;
            case "roles":
                return RoleListView.class;
            case "menus":
                return MenuListView.class;
            case "operationlog":
            case "operation-log":
            case "operation-logs":
                return com.admin.views.operationlog.OperationLogListView.class;
            default:
                // 如果找不到映射，返回 null
                return null;
        }
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

    /**
     * 刷新 UI 文本
     * 切换语言后调用此方法更新所有文本
     */
    private void refreshUIText() {
        // 刷新整个 MainLayout 的文本
        UIRefreshUtil.refreshUIText(this);
        
        // 触发 UI 刷新事件
        UIRefreshUtil.triggerUIRefresh();
        
        // 重新创建导航菜单以更新菜单文本
        if (tabs != null) {
            tabs.removeAll();
            try {
                Long userId = UserUtil.getCurrentUserId();
                if (userId != null) {
                    List<Menu> menuTree = menuService.getMenuTreeByUserId(userId);
                    for (Menu menu : menuTree) {
                        Tab tab = createTabFromMenu(menu);
                        if (tab != null) {
                            tabs.add(tab);
                        }
                    }
                } else {
                    // 如果无法获取用户ID，使用默认菜单
                    tabs.add(createTab(I18NUtil.get("main.layout.dashboard"), DashboardView.class));
                    tabs.add(createTab(I18NUtil.get("main.layout.user.management"), UserListView.class));
                    tabs.add(createTab(I18NUtil.get("main.layout.role.management"), RoleListView.class));
                    tabs.add(createTab(I18NUtil.get("main.layout.menu.management"), MenuListView.class));
                    tabs.add(createTab(I18NUtil.get("main.layout.operation.log"), com.admin.views.operationlog.OperationLogListView.class));
                }
            } catch (Exception e) {
                // 如果加载菜单失败，使用默认菜单
                tabs.add(createTab(I18NUtil.get("main.layout.dashboard"), DashboardView.class));
                tabs.add(createTab(I18NUtil.get("main.layout.user.management"), UserListView.class));
                tabs.add(createTab(I18NUtil.get("main.layout.role.management"), RoleListView.class));
                tabs.add(createTab(I18NUtil.get("main.layout.menu.management"), MenuListView.class));
                tabs.add(createTab(I18NUtil.get("main.layout.operation.log"), com.admin.views.operationlog.OperationLogListView.class));
            }
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
