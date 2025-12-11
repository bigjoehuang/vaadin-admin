package com.admin.component;

import com.admin.util.I18NUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * 带图标的按钮组件
 * 简化创建带图标按钮的过程，符合 Vaadin 24 最佳实践
 * - 自动添加无障碍支持（aria-label）
 * - 支持工具提示（tooltip）
 * - 自动应用 LUMO_ICON 主题变体（仅图标时）
 * - 支持防止重复点击
 *
 * @author Admin
 * @date 2024-01-01
 */
public class IconButton extends Button {

    private boolean iconOnly = false;

    /**
     * 创建带图标的按钮
     *
     * @param text 按钮文本（I18N key）
     * @param icon 图标
     */
    public IconButton(String text, VaadinIcon icon) {
        this(text, icon, true);
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
        String displayText = useI18N ? I18NUtil.get(text) : text;
        
        // 如果文本为空或只有图标，标记为仅图标按钮
        if (displayText == null || displayText.trim().isEmpty()) {
            this.iconOnly = true;
            // 仅图标按钮自动应用 LUMO_ICON 主题变体
            addThemeVariants(ButtonVariant.LUMO_ICON);
            // 为仅图标按钮设置 aria-label（无障碍支持）
            setAriaLabel(displayText != null && !displayText.trim().isEmpty() ? displayText : text);
        } else {
            // 有文本的按钮，设置 aria-label 和 tooltip
            setAriaLabel(displayText);
            setTooltipText(displayText);
        }
    }

    /**
     * 创建仅图标的按钮（无文本）
     *
     * @param icon 图标
     * @param ariaLabel aria-label 文本（I18N key）
     */
    public IconButton(VaadinIcon icon, String ariaLabel) {
        super(new Icon(icon));
        this.iconOnly = true;
        // 仅图标按钮自动应用 LUMO_ICON 主题变体
        addThemeVariants(ButtonVariant.LUMO_ICON);
        // 设置 aria-label 和 tooltip
        String labelText = I18NUtil.get(ariaLabel);
        setAriaLabel(labelText);
        setTooltipText(labelText);
    }

    /**
     * 创建仅图标的按钮（无文本，使用直接文本而非 I18N key）
     *
     * @param icon 图标
     * @param ariaLabel aria-label 文本
     * @param useI18N 是否使用 I18N
     */
    public IconButton(VaadinIcon icon, String ariaLabel, boolean useI18N) {
        super(new Icon(icon));
        this.iconOnly = true;
        // 仅图标按钮自动应用 LUMO_ICON 主题变体
        addThemeVariants(ButtonVariant.LUMO_ICON);
        // 设置 aria-label 和 tooltip
        String labelText = useI18N ? I18NUtil.get(ariaLabel) : ariaLabel;
        setAriaLabel(labelText);
        setTooltipText(labelText);
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

    /**
     * 设置工具提示文本
     *
     * @param tooltipText 工具提示文本（I18N key）
     * @return this
     */
    public IconButton withTooltip(String tooltipText) {
        setTooltipText(I18NUtil.get(tooltipText));
        return this;
    }

    /**
     * 设置工具提示文本（使用直接文本）
     *
     * @param tooltipText 工具提示文本
     * @param useI18N 是否使用 I18N
     * @return this
     */
    public IconButton withTooltip(String tooltipText, boolean useI18N) {
        setTooltipText(useI18N ? I18NUtil.get(tooltipText) : tooltipText);
        return this;
    }

    /**
     * 设置 aria-label
     *
     * @param ariaLabel aria-label 文本（I18N key）
     * @return this
     */
    public IconButton withAriaLabel(String ariaLabel) {
        setAriaLabel(I18NUtil.get(ariaLabel));
        return this;
    }

    /**
     * 设置 aria-label（使用直接文本）
     *
     * @param ariaLabel aria-label 文本
     * @param useI18N 是否使用 I18N
     * @return this
     */
    public IconButton withAriaLabel(String ariaLabel, boolean useI18N) {
        setAriaLabel(useI18N ? I18NUtil.get(ariaLabel) : ariaLabel);
        return this;
    }

    /**
     * 启用防止重复点击
     * 点击后自动禁用按钮，防止重复提交
     *
     * @return this
     */
    public IconButton withDisableOnClick() {
        setDisableOnClick(true);
        return this;
    }

    /**
     * 是否仅为图标按钮
     *
     * @return true 如果仅为图标按钮
     */
    public boolean isIconOnly() {
        return iconOnly;
    }
}





