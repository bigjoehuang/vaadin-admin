package com.admin.util;

/**
 * 密码生成工具类
 * 用于生成 BCrypt 密码哈希
 * 
 * 使用方法：
 * 运行 main 方法，会输出 "admin123" 的 BCrypt 哈希值
 * 
 * @author Admin
 * @date 2024-01-01
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        String password = "admin123";
        String encoded = SecurityUtil.encodePassword(password);
        
        System.out.println("========================================");
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt 哈希: " + encoded);
        System.out.println("========================================");
        System.out.println();
        System.out.println("SQL 更新语句:");
        System.out.println("UPDATE sys_user SET password = '" + encoded + "' WHERE userName = 'admin';");
        System.out.println();
        System.out.println("或者使用 INSERT 语句:");
        System.out.println("INSERT INTO sys_user (userName, password, nickname, email, isEnabled, deleted)");
        System.out.println("VALUES ('admin', '" + encoded + "', '管理员', 'admin@example.com', 1, 0)");
        System.out.println("ON DUPLICATE KEY UPDATE password = '" + encoded + "';");
    }
}






