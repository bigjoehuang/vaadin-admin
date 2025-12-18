package com.admin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;

/**
 * UI 刷新工具类
 * 用于在语言切换后刷新 UI 文本，无需刷新页面
 *
 * @author Admin
 * @date 2024-01-01
 */
public class UIRefreshUtil {

    /**
     * 刷新 UI 中的所有文本
     * 遍历所有组件并更新使用 I18N 的文本
     *
     * @param root 根组件
     */
    public static void refreshUIText(Component root) {
        if (root == null) {
            return;
        }

        // 刷新当前组件
        refreshComponentText(root);

        // 递归刷新子组件
        root.getChildren().forEach(UIRefreshUtil::refreshUIText);
    }

    /**
     * 刷新单个组件的文本
     *
     * @param component 组件
     */
    private static void refreshComponentText(Component component) {
        if (component == null) {
            return;
        }

        // 处理不同类型的组件
        if (component instanceof Button button) {
            refreshButtonText(button);
        } else if (component instanceof H1 h1) {
            refreshHeadingText(h1);
        } else if (component instanceof H2 h2) {
            refreshHeadingText(h2);
        } else if (component instanceof H3 h3) {
            refreshHeadingText(h3);
        } else if (component instanceof H4 h4) {
            refreshHeadingText(h4);
        } else if (component instanceof H5 h5) {
            refreshHeadingText(h5);
        } else if (component instanceof H6 h6) {
            refreshHeadingText(h6);
        } else if (component instanceof Span span) {
            refreshSpanText(span);
        } else if (component instanceof Div div) {
            refreshDivText(div);
        } else if (component instanceof Paragraph paragraph) {
            refreshParagraphText(paragraph);
        } else if (component instanceof Label label) {
            refreshLabelText(label);
        } else if (component instanceof Tab tab) {
            refreshTabText(tab);
        } else if (component instanceof RouterLink routerLink) {
            refreshRouterLinkText(routerLink);
        } else if (component instanceof HasText hasTextComponent) {
            // 对于其他实现了 HasText 接口的组件，触发重新渲染
            triggerComponentUpdate(component);
        }

        // 更新组件的辅助属性
        updateAccessibilityAttributes(component);
        
        // 触发组件重新渲染
        triggerComponentUpdate(component);
    }

    /**
     * 刷新 Button 组件的文本
     *
     * @param button Button 组件
     */
    private static void refreshButtonText(Button button) {
        // Button 的文本通常是在创建时设置的，切换语言后需要重新设置
        // 但由于我们无法获取原始的 I18N key，所以只能触发重新渲染
        triggerComponentUpdate(button);
        
        // 更新 aria-label 属性
        String ariaLabel = button.getElement().getAttribute("aria-label");
        if (ariaLabel != null && !ariaLabel.isEmpty()) {
            // 尝试重新获取 aria-label 的国际化文本
            // 注意：这只在 aria-label 直接使用 I18NUtil.get() 设置时有效
            // 如果 aria-label 包含其他文本，这种方式可能会失效
            if (ariaLabel.startsWith("main.layout.") || 
                ariaLabel.startsWith("locale.") ||
                ariaLabel.startsWith("status.")) {
                button.setAriaLabel(I18NUtil.get(ariaLabel));
            }
        }
    }

    /**
     * 刷新标题组件的文本（支持所有标题级别：H1-H6）
     *
     * @param heading 标题组件
     */
    private static void refreshHeadingText(Component heading) {
        // 标题的文本通常是在创建时设置的，切换语言后需要重新设置
        // 但由于我们无法获取原始的 I18N key，所以只能触发重新渲染
        triggerComponentUpdate(heading);
    }

    /**
     * 刷新 Div 组件的文本
     *
     * @param div Div 组件
     */
    private static void refreshDivText(Div div) {
        triggerComponentUpdate(div);
    }

    /**
     * 刷新 Paragraph 组件的文本
     *
     * @param paragraph Paragraph 组件
     */
    private static void refreshParagraphText(Paragraph paragraph) {
        triggerComponentUpdate(paragraph);
    }

    /**
     * 刷新 Label 组件的文本
     *
     * @param label Label 组件
     */
    private static void refreshLabelText(Label label) {
        triggerComponentUpdate(label);
    }

    /**
     * 刷新 Span 组件的文本
     *
     * @param span Span 组件
     */
    private static void refreshSpanText(Span span) {
        // Span 的文本通常是在创建时设置的，切换语言后需要重新设置
        // 但由于我们无法获取原始的 I18N key，所以只能触发重新渲染
        triggerComponentUpdate(span);
    }

    /**
     * 刷新 Tab 组件的文本
     *
     * @param tab Tab 组件
     */
    private static void refreshTabText(Tab tab) {
        // Tab 通常包含 RouterLink，所以需要刷新其子组件
        tab.getChildren().forEach(UIRefreshUtil::refreshComponentText);
        triggerComponentUpdate(tab);
    }

    /**
     * 刷新 RouterLink 组件的文本
     *
     * @param routerLink RouterLink 组件
     */
    private static void refreshRouterLinkText(RouterLink routerLink) {
        // RouterLink 的文本通常是在创建时设置的，切换语言后需要重新设置
        // 但由于我们无法获取原始的 I18N key，所以只能触发重新渲染
        triggerComponentUpdate(routerLink);
    }

    /**
     * 更新组件的辅助属性
     * 包括 aria-label、title 等需要国际化的属性
     *
     * @param component 组件
     */
    private static void updateAccessibilityAttributes(Component component) {
        if (component == null) {
            return;
        }
        
        // 更新 aria-label 属性
        String ariaLabel = component.getElement().getAttribute("aria-label");
        if (ariaLabel != null && !ariaLabel.isEmpty()) {
            // 尝试重新获取 aria-label 的国际化文本
            if (ariaLabel.startsWith("main.layout.") || 
                ariaLabel.startsWith("locale.") ||
                ariaLabel.startsWith("status.") ||
                ariaLabel.startsWith("button.") ||
                ariaLabel.startsWith("menu.")) {
                component.getElement().setAttribute("aria-label", I18NUtil.get(ariaLabel));
            }
        }
        
        // 更新 title 属性
        String title = component.getElement().getAttribute("title");
        if (title != null && !title.isEmpty()) {
            // 尝试重新获取 title 的国际化文本
            if (title.startsWith("main.layout.") || 
                title.startsWith("locale.") ||
                title.startsWith("status.") ||
                title.startsWith("tooltip.")) {
                component.getElement().setAttribute("title", I18NUtil.get(title));
            }
        }
        
        // 更新 placeholder 属性（适用于输入组件）
        String placeholder = component.getElement().getAttribute("placeholder");
        if (placeholder != null && !placeholder.isEmpty()) {
            // 尝试重新获取 placeholder 的国际化文本
            if (placeholder.startsWith("input.") || 
                placeholder.startsWith("search.") ||
                placeholder.startsWith("filter.")) {
                component.getElement().setAttribute("placeholder", I18NUtil.get(placeholder));
            }
        }
    }

    /**
     * 触发组件重新渲染
     *
     * @param component 组件
     */
    private static void triggerComponentUpdate(Component component) {
        if (component == null) {
            return;
        }

        // 触发组件的 property change 事件，强制重新渲染
        component.getElement().executeJs("this.requestUpdate();");
    }

    /**
     * 触发 UI 刷新
     * 通过 JavaScript 触发页面重新渲染
     */
    public static void triggerUIRefresh() {
        UI currentUI = UI.getCurrent();
        if (currentUI != null) {
            // 使用 JavaScript 触发页面重新渲染
            // 注意：这不会完全刷新页面，只是触发组件重新渲染
            // 安全地调用 requestUpdate，只对支持该方法的元素调用
            currentUI.getPage().executeJs(
                "// 触发所有组件重新渲染\n" +
                "document.querySelectorAll('vaadin-button, vaadin-text-field, vaadin-combo-box, vaadin-select, vaadin-grid, " +
                "vaadin-label, vaadin-checkbox, vaadin-radio-button, vaadin-tabs, vaadin-menu-bar, " +
                "vaadin-dialog, vaadin-form-layout, vaadin-date-picker, vaadin-time-picker, vaadin-text-area, " +
                "vaadin-number-field, vaadin-password-field, vaadin-email-field, vaadin-date-time-picker, " +
                "vaadin-upload, vaadin-progress-bar, vaadin-notification, vaadin-context-menu, " +
                "vaadin-split-layout, vaadin-vertical-layout, vaadin-horizontal-layout, vaadin-scroller, " +
                "vaadin-app-layout, vaadin-login-form, vaadin-message-input, vaadin-message-list, " +
                "vaadin-chart, vaadin-accordion, vaadin-details, vaadin-integer-field, vaadin-big-decimal-field').forEach(el => {\n" +
                "  try {\n" +
                "    // 检查元素是否有 requestUpdate 方法（Lit 元素）\n" +
                "    if (typeof el.requestUpdate === 'function') {\n" +
                "      el.requestUpdate();\n" +
                "    }\n" +
                "    // 对于其他元素，尝试触发自定义事件来刷新\n" +
                "    if (el.dispatchEvent) {\n" +
                "      el.dispatchEvent(new CustomEvent('locale-change', { bubbles: true }));\n" +
                "    }\n" +
                "  } catch (e) {\n" +
                "    // 忽略错误，继续处理下一个元素\n" +
                "    console.debug('Failed to update element:', e);\n" +
                "  }\n" +
                "});"
            );
        }
    }
}

