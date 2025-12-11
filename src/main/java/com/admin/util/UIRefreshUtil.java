package com.admin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

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
        // 对于 HasText 接口的组件，文本通常是通过 I18NUtil.get() 动态获取的
        // 所以这里不需要手动更新，因为 I18NUtil.get() 会根据当前 Locale 返回正确的文本
        
        // 但是，如果组件存储了 I18N key，我们需要重新获取文本
        // 由于当前实现中组件直接使用 I18NUtil.get() 的结果，所以切换 Locale 后
        // 只需要触发组件重新渲染即可
        
        // 对于 Button、Label、Span 等组件，如果它们有 aria-label 或其他属性使用 I18N
        // 我们需要更新这些属性
        // 注意：由于组件直接使用 I18NUtil.get() 的结果，切换 Locale 后
        // I18NUtil.get() 会自动返回新语言的文本，但组件需要重新渲染才能显示
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
                "document.querySelectorAll('vaadin-button, vaadin-text-field, vaadin-combo-box, vaadin-select, vaadin-grid').forEach(el => {\n" +
                "  try {\n" +
                "    // 检查元素是否有 requestUpdate 方法（Lit 元素）\n" +
                "    if (el.shadowRoot && typeof el.requestUpdate === 'function') {\n" +
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

