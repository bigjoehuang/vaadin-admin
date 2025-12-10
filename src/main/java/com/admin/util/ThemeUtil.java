package com.admin.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主题切换工具类
 * 用于切换明暗主题
 *
 * @author Admin
 * @date 2024-01-01
 */
public class ThemeUtil {

    private static final Logger log = LoggerFactory.getLogger(ThemeUtil.class);

    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";

    /**
     * 初始化主题
     * 从 localStorage 恢复用户保存的主题偏好
     */
    public static void initTheme() {
        UI ui = UI.getCurrent();
        if (ui != null) {
            Page page = ui.getPage();
            page.executeJs("if (window.initTheme) { window.initTheme(); }");
        }
    }

    /**
     * 切换主题
     * 在亮色和暗色主题之间切换
     */
    public static void toggleTheme() {
        UI ui = UI.getCurrent();
        if (ui != null) {
            Page page = ui.getPage();
            page.executeJs("if (window.toggleTheme) { window.toggleTheme(); }");
        }
    }

    /**
     * 设置指定主题
     *
     * @param theme 主题名称，"light" 或 "dark"
     */
    public static void setTheme(String theme) {
        if (THEME_LIGHT.equals(theme) || THEME_DARK.equals(theme)) {
            UI ui = UI.getCurrent();
            if (ui != null) {
                Page page = ui.getPage();
                page.executeJs("if (window.setTheme) { window.setTheme($0); }", theme);
            }
        } else {
            log.warn("无效的主题名称: {}", theme);
        }
    }

    /**
     * 获取当前主题
     *
     * @return 当前主题名称，"light" 或 "dark"
     */
    public static String getCurrentTheme() {
        // 这个方法需要通过 JavaScript 回调获取，这里返回默认值
        // 实际使用时应该通过 JavaScript 回调获取
        return THEME_LIGHT;
    }
}

