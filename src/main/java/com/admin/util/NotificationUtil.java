package com.admin.util;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * 通知工具类
 * 用于显示成功、错误、警告等信息提示
 *
 * @author Admin
 * @date 2024-01-01
 */
public class NotificationUtil {

    /**
     * 显示成功通知
     *
     * @param message 消息内容
     */
    public static void showSuccess(String message) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }

    /**
     * 显示错误通知
     *
     * @param message 消息内容
     */
    public static void showError(String message) {
        Notification notification = new Notification(message, 5000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }

    /**
     * 显示警告通知
     *
     * @param message 消息内容
     */
    public static void showWarning(String message) {
        Notification notification = new Notification(message, 4000);
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }

    /**
     * 显示信息通知
     *
     * @param message 消息内容
     */
    public static void showInfo(String message) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}






