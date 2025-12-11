package com.admin.component;

import com.admin.util.I18NUtil;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

/**
 * 确认对话框工具类
 * 提供统一的确认对话框创建方法
 *
 * @author Admin
 * @date 2024-01-01
 */
public class ConfirmDialogUtil {

    /**
     * 创建删除确认对话框
     *
     * @param entityName 实体名称（用于 I18N）
     * @param displayName 显示名称
     * @param onConfirm 确认回调
     * @return ConfirmDialog
     */
    public static ConfirmDialog createDeleteDialog(String entityName, String displayName, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(I18NUtil.get("confirm.delete.title"));
        dialog.setText(I18NUtil.get("confirm.delete.text", entityName, displayName));
        dialog.setConfirmText(I18NUtil.get("common.delete"));
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelText(I18NUtil.get("common.cancel"));
        dialog.setCancelButtonTheme("tertiary");
        dialog.setCancelable(true);

        dialog.addConfirmListener(e -> onConfirm.run());
        return dialog;
    }

    /**
     * 创建批量删除确认对话框
     *
     * @param entityName 实体名称（用于 I18N）
     * @param count 数量
     * @param names 名称列表（用分隔符连接）
     * @param onConfirm 确认回调
     * @return ConfirmDialog
     */
    public static ConfirmDialog createBatchDeleteDialog(String entityName, int count, String names, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(I18NUtil.get("confirm.batch.delete.title"));
        
        // 使用通用的批量删除确认文本
        String confirmKey = entityName.toLowerCase().replace(" ", ".") + ".batch.delete.confirm";
        String confirmText;
        try {
            confirmText = I18NUtil.get(confirmKey, count, names);
        } catch (Exception e) {
            // 如果找不到特定的 key，使用通用格式
            confirmText = I18NUtil.get("confirm.batch.delete.text", count, entityName) + "\n" + names;
        }
        
        dialog.setText(confirmText);
        dialog.setConfirmText(I18NUtil.get("common.delete"));
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelText(I18NUtil.get("common.cancel"));
        dialog.setCancelButtonTheme("tertiary");
        dialog.setCancelable(true);

        dialog.addConfirmListener(e -> onConfirm.run());
        return dialog;
    }

    /**
     * 创建批量启用确认对话框
     *
     * @param entityName 实体名称（用于 I18N）
     * @param count 数量
     * @param onConfirm 确认回调
     * @return ConfirmDialog
     */
    public static ConfirmDialog createBatchEnableDialog(String entityName, int count, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(I18NUtil.get("confirm.batch.enable.title"));
        dialog.setText(I18NUtil.get("confirm.batch.enable.text", count, entityName));
        dialog.setConfirmText(I18NUtil.get("common.enabled"));
        dialog.setConfirmButtonTheme("primary");
        dialog.setCancelText(I18NUtil.get("common.cancel"));
        dialog.setCancelButtonTheme("tertiary");
        dialog.setCancelable(true);

        dialog.addConfirmListener(e -> onConfirm.run());
        return dialog;
    }

    /**
     * 创建批量禁用确认对话框
     *
     * @param entityName 实体名称（用于 I18N）
     * @param count 数量
     * @param onConfirm 确认回调
     * @return ConfirmDialog
     */
    public static ConfirmDialog createBatchDisableDialog(String entityName, int count, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(I18NUtil.get("confirm.batch.disable.title"));
        dialog.setText(I18NUtil.get("confirm.batch.disable.text", count, entityName));
        dialog.setConfirmText(I18NUtil.get("common.disabled"));
        dialog.setConfirmButtonTheme("primary");
        dialog.setCancelText(I18NUtil.get("common.cancel"));
        dialog.setCancelButtonTheme("tertiary");
        dialog.setCancelable(true);

        dialog.addConfirmListener(e -> onConfirm.run());
        return dialog;
    }

    /**
     * 创建自定义确认对话框
     *
     * @param header 标题
     * @param text 内容
     * @param confirmText 确认按钮文本
     * @param cancelText 取消按钮文本
     * @param onConfirm 确认回调
     * @return ConfirmDialog
     */
    public static ConfirmDialog createCustomDialog(String header, String text, String confirmText, String cancelText, Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(header);
        dialog.setText(text);
        dialog.setConfirmText(confirmText);
        dialog.setConfirmButtonTheme("primary");
        dialog.setCancelText(cancelText);
        dialog.setCancelButtonTheme("tertiary");
        dialog.setCancelable(true);

        dialog.addConfirmListener(e -> onConfirm.run());
        return dialog;
    }
}


