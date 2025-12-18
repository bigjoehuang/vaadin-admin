package com.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Spring Security 配置
 *
 * @author Admin
 * @date 2024-01-01
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 允许访问登录页面和静态资源
                .requestMatchers(
                    "/",
                    "/login",
                    "/VAADIN/**",
                    "/icons/**",
                    "/images/**",
                    "/themes/**",
                    "/frontend/**",
                    "/sw.js",
                    "/sw-runtime-resources-precache.js",
                    "/manifest.webmanifest"
                ).permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            // 启用CSRF防护，使用CookieCsrfTokenRepository以支持Vaadin和REST API
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // 忽略静态资源和API的CSRF检查
                .ignoringRequestMatchers("/VAADIN/**", "/api/**", "/icons/**", "/images/**", "/themes/**", "/frontend/**", "/sw.js", "/sw-runtime-resources-precache.js", "/manifest.webmanifest")
            )
            // 配置表单登录
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
            )
            // 配置会话管理
            .sessionManagement(session -> session
                // 设置会话超时时间为30分钟
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
                .and()
                .sessionFixation().newSession()
            )
            // 添加安全头
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; frame-ancestors 'self';")
                )
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

