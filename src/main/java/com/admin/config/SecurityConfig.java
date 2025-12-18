package com.admin.config;

import com.admin.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 配置
 *
 * @author Admin
 * @date 2024-01-01
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    /**
     * Spring Security 与 Vaadin 集成配置
     *
     * <p>使用 Vaadin 提供的 {@link VaadinWebSecurity}，确保 Vaadin 内部端点和
     * Spring Security 协同工作，避免客户端期望 JSON 时收到 HTML 导致的
     * "Invalid JSON response from server" 错误。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 如果有额外的开放端点（例如 REST API），在这里配置：
        // http.authorizeHttpRequests(auth -> auth.requestMatchers("/api/**").permitAll());

        // 让 Vaadin 配置自身需要的安全规则（包括 CSRF、静态资源等）
        super.configure(http);

        // 使用 Vaadin 登录视图作为 Spring Security 的登录页面
        setLoginView(http, LoginView.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

