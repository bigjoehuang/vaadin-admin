package com.admin.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

/**
 * 国际化提供者
 * 实现 Vaadin I18NProvider 接口，提供中英文国际化支持
 *
 * @author Admin
 * @date 2024-01-01
 */
@Component
public class AdminI18NProvider implements I18NProvider {

    private static final String BUNDLE_PREFIX = "i18n/messages";
    private static final Locale DEFAULT_LOCALE = Locale.US; // 默认英文
    private static final List<Locale> PROVIDED_LOCALES = Arrays.asList(
            Locale.SIMPLIFIED_CHINESE, // zh_CN
            Locale.US // en_US
    );

    private final Map<Locale, ResourceBundle> bundles = new HashMap<>();

    public AdminI18NProvider() {
        // 预加载所有语言包
        for (Locale locale : PROVIDED_LOCALES) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(
                        BUNDLE_PREFIX,
                        locale,
                        ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES)
                );
                bundles.put(locale, bundle);
            } catch (MissingResourceException e) {
                // 如果某个语言包不存在，记录警告但继续
                System.err.println("Warning: Resource bundle not found for locale: " + locale);
            }
        }
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return PROVIDED_LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            return null;
        }

        // 如果 locale 为 null，使用默认 locale
        if (locale == null) {
            locale = DEFAULT_LOCALE;
        }

        // 如果请求的 locale 不支持，尝试使用默认 locale
        if (!PROVIDED_LOCALES.contains(locale)) {
            locale = DEFAULT_LOCALE;
        }

        ResourceBundle bundle = bundles.get(locale);
        if (bundle == null) {
            bundle = bundles.get(DEFAULT_LOCALE);
        }

        if (bundle == null) {
            return key; // 如果连默认语言包都没有，返回 key
        }

        try {
            String value = bundle.getString(key);
            if (params.length > 0) {
                // 使用 MessageFormat 进行参数替换
                return MessageFormat.format(value, params);
            }
            return value;
        } catch (MissingResourceException e) {
            // 如果找不到 key，返回 key 本身
            return key;
        }
    }
}

