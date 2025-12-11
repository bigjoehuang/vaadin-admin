package com.admin.component;

import com.admin.util.I18NUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * 带图标的按钮组件
 * 简化创建带图标按钮的过程
 *
 * @author Admin
 * @date 2024-01-01
 */
public class IconButton extends Button {

    /**
     * 创建带图标的按钮
     *
     * @param text 按钮文本（I18N key）
     * @param icon 图标
     */
    public IconButton(String text, VaadinIcon icon) {
        super(I18NUtil.get(text), new Icon(icon));
    }

    /**
     * 创建带图标的按钮（使用文本而非 I18N key）
     *
     * @param text 按钮文本
     * @param icon 图标
     * @param useI18N 是否使用 I18N
     */
    public IconButton(String text, VaadinIcon icon, boolean useI18N) {
        super(useI18N ? I18NUtil.get(text) : text, new Icon(icon));
    }

    /**
     * 添加主题变体
     *
     * @param variants 主题变体
     * @return this
     */
    public IconButton withVariants(ButtonVariant... variants) {
        addThemeVariants(variants);
        return this;
    }
}




