package com.admin.util;

import com.admin.i18n.AdminI18NProvider;
import com.vaadin.flow.component.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化工具类
 * 提供便捷的国际化文本获取方法
 *
 * @author Admin
 * @date 2024-01-01
 */
@Component
public class I18NUtil {

    private static AdminI18NProvider i18nProvider;

    @Autowired
    public void setI18NProvider(AdminI18NProvider i18nProvider) {
        I18NUtil.i18nProvider = i18nProvider;
    }

    /**
     * 获取国际化文本
     *
     * @param key 国际化 key
     * @return 国际化文本
     */
    public static String get(String key) {
        return get(key, getCurrentLocale());
    }

    /**
     * 获取国际化文本（带参数）
     *
     * @param key    国际化 key
     * @param params 参数
     * @return 国际化文本
     */
    public static String get(String key, Object... params) {
        return get(key, getCurrentLocale(), params);
    }

    /**
     * 获取国际化文本（指定 Locale）
     *
     * @param key    国际化 key
     * @param locale Locale
     * @param params 参数
     * @return 国际化文本
     */
    public static String get(String key, Locale locale, Object... params) {
        if (i18nProvider == null) {
            return key; // 如果 provider 未初始化，返回 key
        }
        return i18nProvider.getTranslation(key, locale, params);
    }

    /**
     * 获取当前 UI 的 Locale
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
        // 如果无法获取 UI 的 Locale，尝试从 LocaleUtil 获取
        return LocaleUtil.getCurrentLocale();
    }

    /**
     * 设置当前 UI 的 Locale
     *
     * @param locale Locale
     */
    public static void setCurrentLocale(Locale locale) {
        UI currentUI = UI.getCurrent();
        if (currentUI != null) {
            currentUI.setLocale(locale);
        }
    }
}

