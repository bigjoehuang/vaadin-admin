package com.admin.generator;

import lombok.Data;

/**
 * 代码生成器配置
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
public class GeneratorConfig {
    /**
     * 作者
     */
    private String author = "Admin";

    /**
     * 包名
     */
    private String packageName = "com.admin";

    /**
     * 模块名
     */
    private String moduleName = "system";

    /**
     * 表名
     */
    private String tableName;

    /**
     * 实体类名
     */
    private String entityName;

    /**
     * 表注释
     */
    private String tableComment;
}


