package com.admin.constant;

/**
 * 状态常量类
 * 用于状态比较，避免使用 I18N 文本进行比较
 * 
 * I18N 只用于展示，不用于逻辑判断
 * 
 * @author Admin
 * @date 2024-01-01
 */
public class StatusConstant {
    
    /**
     * 全部（用于筛选）
     */
    public static final String ALL = "ALL";
    
    /**
     * 启用
     */
    public static final String ENABLED = "ENABLED";
    
    /**
     * 禁用
     */
    public static final String DISABLED = "DISABLED";
    
    private StatusConstant() {
        // 工具类，禁止实例化
    }
}




