package com.admin.util;

import com.vaadin.flow.component.UI;

import java.util.Locale;

/**
 * 语言切换工具类
 * 管理语言偏好和切换逻辑
 *
 * @author Admin
 * @date 2024-01-01
 */
public class LocaleUtil {

    private static final String LOCALE_STORAGE_KEY = "vaadin-admin-locale";
    private static final Locale DEFAULT_LOCALE = Locale.US; // 默认英文
    private static final Locale ZH_CN = Locale.SIMPLIFIED_CHINESE;
    private static final Locale EN_US = Locale.US;

    /**
     * 获取当前语言
     *
     * @return Locale
     */
    public static Locale getCurrentLocale() {
        UI currentUI = UI.getCurrent();
        if (currentUI != null) {
            Locale locale = currentUI.getLocale();
            if (locale != null) {
                return locale;
            }
        }

        // 从 localStorage 读取
        if (currentUI != null) {
            currentUI.getPage().executeJs(
                    "return localStorage.getItem($0);",
                    LOCALE_STORAGE_KEY
            ).then(String.class, localeStr -> {
                if (localeStr != null && !localeStr.isEmpty()) {
                    Locale savedLocale = parseLocale(localeStr);
                    if (savedLocale != null && currentUI != null) {
                        currentUI.setLocale(savedLocale);
                    }
                }
            });
        }

        return DEFAULT_LOCALE;
    }

    /**
     * 设置语言
     *
     * @param locale Locale
     */
    public static void setLocale(Locale locale) {
        if (locale == null) {
            locale = DEFAULT_LOCALE;
        }

        UI currentUI = UI.getCurrent();
        if (currentUI != null) {
            // 设置 UI 的 Locale
            currentUI.setLocale(locale);

            // 保存到 localStorage
            String localeStr = localeToString(locale);
            currentUI.getPage().executeJs(
                    "localStorage.setItem($0, $1);",
                    LOCALE_STORAGE_KEY,
                    localeStr
            );
        }
    }

    /**
     * 初始化语言
     * 从 localStorage 读取，如果没有则使用浏览器语言或默认语言
     */
    public static void initLocale() {
        UI currentUI = UI.getCurrent();
        if (currentUI == null) {
            return;
        }

        // 从 localStorage 读取
        currentUI.getPage().executeJs(
                "return localStorage.getItem($0);",
                LOCALE_STORAGE_KEY
        ).then(String.class, localeStr -> {
            Locale locale = DEFAULT_LOCALE;
            if (localeStr != null && !localeStr.isEmpty()) {
                Locale savedLocale = parseLocale(localeStr);
                if (savedLocale != null) {
                    locale = savedLocale;
                }
            } else {
                // 如果没有保存的语言偏好，尝试从浏览器语言获取
                locale = getBrowserLocale();
            }
            currentUI.setLocale(locale);
        });
    }

    /**
     * 切换语言（中文和英文之间切换）
     */
    public static void toggleLocale() {
        Locale current = getCurrentLocale();
        if (ZH_CN.equals(current)) {
            setLocale(EN_US);
        } else {
            setLocale(ZH_CN);
        }
    }

    /**
     * 获取浏览器语言
     *
     * @return Locale
     */
    private static Locale getBrowserLocale() {
        // 简化实现：直接返回默认语言
        // 实际的浏览器语言检测在 AppShell 中通过 JavaScript 完成
        return DEFAULT_LOCALE;
    }

    /**
     * 将 Locale 转换为字符串
     *
     * @param locale Locale
     * @return 字符串
     */
    private static String localeToString(Locale locale) {
        if (locale == null) {
            return "en_US";
        }
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    /**
     * 将字符串解析为 Locale
     *
     * @param localeStr 字符串
     * @return Locale
     */
    private static Locale parseLocale(String localeStr) {
        if (localeStr == null || localeStr.isEmpty()) {
            return DEFAULT_LOCALE;
        }

        // 使用 Locale.forLanguageTag 或 Locale.Builder 替代已弃用的构造函数
        try {
            // 将 "zh_CN" 格式转换为 "zh-CN" 格式
            String normalized = localeStr.replace("_", "-");
            Locale locale = Locale.forLanguageTag(normalized);
            // 如果解析失败，使用默认语言
            if (locale.getLanguage().isEmpty()) {
                return DEFAULT_LOCALE;
            }
            return locale;
        } catch (Exception e) {
            return DEFAULT_LOCALE;
        }
    }
}

