package com.admin.views;

import com.admin.service.MenuService;
import com.admin.service.RoleService;
import com.admin.service.UserService;
import com.admin.util.I18NUtil;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * 仪表盘视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements AuthenticatedRoute, HasDynamicTitle {
    // #region agent log
    {
        try {
            java.io.FileWriter fw = new java.io.FileWriter("/Users/hjoe/ai-creation/vaadin-admin/.cursor/debug.log", true);
            fw.write(String.format("{\"id\":\"log_%d_dash_init\",\"timestamp\":%d,\"location\":\"DashboardView.java:27\",\"message\":\"DashboardView initialized\",\"data\":{\"pageTitle\":\"${page.dashboard}\",\"i18nTitle\":\"%s\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n", System.currentTimeMillis(), System.currentTimeMillis(), I18NUtil.get("page.dashboard")));
            fw.close();
        } catch (Exception e) {}
    }
    // #endregion

    private final UserService userService;
    private final RoleService roleService;
    private final MenuService menuService;

    public DashboardView(UserService userService, RoleService roleService, MenuService menuService) {
        this.userService = userService;
        this.roleService = roleService;
        this.menuService = menuService;

        addClassName("dashboard-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // 标题
        H2 title = new H2(I18NUtil.get("dashboard.title"));
        title.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Bottom.LARGE
        );
        add(title);

        // 统计卡片
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        statsLayout.addClassNames(LumoUtility.Margin.Bottom.LARGE);

        // 用户统计卡片
        VerticalLayout userCard = createStatCard(
                I18NUtil.get("dashboard.user.total"),
                String.valueOf(userService.listUsers().size()),
                VaadinIcon.USERS,
                "var(--lumo-primary-color)"
        );
        userCard.addClassNames("stat-card", "stat-card-primary");

        // 角色统计卡片
        VerticalLayout roleCard = createStatCard(
                I18NUtil.get("dashboard.role.total"),
                String.valueOf(roleService.listRoles().size()),
                VaadinIcon.SHIELD,
                "var(--lumo-success-color)"
        );
        roleCard.addClassNames("stat-card", "stat-card-success");

        // 菜单统计卡片
        VerticalLayout menuCard = createStatCard(
                I18NUtil.get("dashboard.menu.total"),
                String.valueOf(menuService.listMenus().size()),
                VaadinIcon.MENU,
                "var(--lumo-warning-color)"
        );
        menuCard.addClassNames("stat-card", "stat-card-warning");

        // 系统状态卡片
        VerticalLayout systemCard = createStatCard(
                I18NUtil.get("dashboard.system.status"),
                I18NUtil.get("dashboard.system.running"),
                VaadinIcon.CHECK_CIRCLE,
                "var(--lumo-success-color)"
        );
        systemCard.addClassNames("stat-card", "stat-card-info");

        statsLayout.add(userCard, roleCard, menuCard, systemCard);
        statsLayout.setFlexGrow(1, userCard, roleCard, menuCard, systemCard);

        add(statsLayout);

        // 快速操作区域
        H3 quickActionsTitle = new H3(I18NUtil.get("dashboard.quick.actions"));
        quickActionsTitle.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Top.LARGE,
                LumoUtility.Margin.Bottom.MEDIUM
        );
        add(quickActionsTitle);

        HorizontalLayout quickActionsLayout = new HorizontalLayout();
        quickActionsLayout.setSpacing(true);
        quickActionsLayout.setWidthFull();

        // 这里可以添加快速操作按钮，例如：
        // Button addUserButton = new Button("添加用户", VaadinIcon.PLUS.create());
        // addUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // quickActionsLayout.add(addUserButton);

        add(quickActionsLayout);
    }

    /**
     * 创建统计卡片
     *
     * @param title     标题
     * @param value     数值
     * @param icon      图标
     * @param iconColor 图标颜色
     * @return 卡片布局
     */
    private VerticalLayout createStatCard(String title, String value, VaadinIcon icon, String iconColor) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                "stat-card-content"
        );
        card.setSpacing(true);
        card.setPadding(true);

        // 图标和标题
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon iconComponent = icon.create();
        iconComponent.setSize("24px");
        iconComponent.getStyle().set("color", iconColor);

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY
        );

        header.add(iconComponent, titleSpan);
        header.setFlexGrow(1, titleSpan);

        // 数值
        Span valueSpan = new Span(value);
        valueSpan.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Margin.Top.SMALL
        );

        card.add(header, valueSpan);
        card.setAlignItems(FlexComponent.Alignment.STRETCH);

        return card;
    }

    @Override
    public String getPageTitle() {
        return I18NUtil.get("page.dashboard");
    }
}

