package com.admin.views;

import com.admin.util.I18NUtil;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 登录视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Route("/login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {
    // #region agent log
    {
        try {
            java.io.FileWriter fw = new java.io.FileWriter("/Users/hjoe/ai-creation/vaadin-admin/.cursor/debug.log", true);
            fw.write(String.format("{\"id\":\"log_%d_login_init\",\"timestamp\":%d,\"location\":\"LoginView.java:23\",\"message\":\"LoginView initialized\",\"data\":{\"pageTitle\":\"${page.login}\",\"i18nTitle\":\"%s\"},\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\"}\n", System.currentTimeMillis(), System.currentTimeMillis(), I18NUtil.get("page.login")));
            fw.close();
        } catch (Exception e) {}
    }
    // #endregion

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // 设置登录表单的 action 为 Spring Security 的登录处理 URL
        login.setAction("login");

        add(new H1(I18NUtil.get("login.app.name")), login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // 如果用户已经登录，重定向到主页
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && 
            authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            beforeEnterEvent.forwardTo(DashboardView.class);
            return;
        }

        // 如果登录失败，显示错误信息
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }

    @Override
    public String getPageTitle() {
        return I18NUtil.get("page.login");
    }
}
