package com.admin.views;

import com.admin.util.I18NUtil;
import com.admin.util.NotificationUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;

/**
 * 密码重置视图
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Route("reset-password")
@AnonymousAllowed
public class PasswordResetView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private TextField userNameField;
    private EmailField emailField;
    private Button resetButton;
    private Button backButton;

    public PasswordResetView() {
        addClassName("reset-password-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setSpacing(true);
        
        // 创建页面标题
        H1 title = new H1(I18NUtil.get("reset.password.title"));
        
        // 创建说明文字
        Paragraph description = new Paragraph(I18NUtil.get("reset.password.description"));
        
        // 创建用户名输入框
        userNameField = new TextField(I18NUtil.get("user.userName"));
        userNameField.setPlaceholder(I18NUtil.get("user.placeholder.userName"));
        userNameField.setWidth("300px");
        userNameField.setClearButtonVisible(true);
        
        // 创建邮箱输入框
        emailField = new EmailField(I18NUtil.get("user.email"));
        emailField.setPlaceholder(I18NUtil.get("user.placeholder.email"));
        emailField.setWidth("300px");
        emailField.setClearButtonVisible(true);
        
        // 创建重置按钮
        resetButton = new Button(I18NUtil.get("reset.password.button"), new Icon(VaadinIcon.REFRESH));
        resetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        resetButton.setWidth("300px");
        resetButton.setDisableOnClick(true);
        resetButton.addClickListener(e -> handleResetPassword());
        
        // 创建返回登录按钮
        backButton = new Button(I18NUtil.get("reset.password.back"), new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.setWidth("300px");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        
        // 添加组件到布局
        add(title, description, userNameField, emailField, resetButton, backButton);
    }
    
    /**
     * 处理密码重置请求
     */
    private void handleResetPassword() {
        try {
            String userName = userNameField.getValue();
            String email = emailField.getValue();
            
            // 验证输入
            if ((userName == null || userName.trim().isEmpty()) && (email == null || email.trim().isEmpty())) {
                NotificationUtil.showError(I18NUtil.get("reset.password.validation.required"));
                resetButton.setEnabled(true);
                return;
            }
            
            // 调用服务层方法处理密码重置
            // 这里暂时模拟实现，实际项目中应该发送邮件
            NotificationUtil.showSuccess(I18NUtil.get("reset.password.success"));
            
            // 重置按钮恢复可用
            resetButton.setEnabled(true);
            
            // 重定向到登录页面
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        } catch (Exception e) {
            log.error("密码重置失败: {}", e.getMessage());
            NotificationUtil.showError(I18NUtil.get("reset.password.failed") + ": " + e.getMessage());
            resetButton.setEnabled(true);
        }
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // 可以添加一些前置处理逻辑
    }
    
    @Override
    public String getPageTitle() {
        return I18NUtil.get("page.reset.password");
    }
}
