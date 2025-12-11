package com.admin.component;

import com.admin.util.I18NUtil;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * 状态徽章组件
 * 用于显示启用/禁用等状态
 *
 * @author Admin
 * @date 2024-01-01
 */
public class StatusBadge extends Span {

    /**
     * 创建启用状态徽章
     *
     * @return StatusBadge
     */
    public static StatusBadge enabled() {
        StatusBadge badge = new StatusBadge();
        badge.setEnabled(true);
        return badge;
    }

    /**
     * 创建禁用状态徽章
     *
     * @return StatusBadge
     */
    public static StatusBadge disabled() {
        StatusBadge badge = new StatusBadge();
        badge.setEnabled(false);
        return badge;
    }

    /**
     * 设置状态
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        removeAll();
        getStyle().clear();

        Icon icon = new Icon(enabled ? VaadinIcon.CHECK_CIRCLE : VaadinIcon.CLOSE_CIRCLE);
        icon.setSize("16px");
        if (enabled) {
            icon.getStyle().set("color", "var(--lumo-success-color)");
        } else {
            icon.getStyle().set("color", "var(--lumo-error-color)");
        }

        Span text = new Span(enabled ? I18NUtil.get("common.enabled") : I18NUtil.get("common.disabled"));
        text.getStyle().set("font-size", "var(--lumo-font-size-s)");
        text.getStyle().set("margin-left", "4px");

        add(icon, text);
        getStyle().set("display", "flex");
        getStyle().set("align-items", "center");
    }
}


