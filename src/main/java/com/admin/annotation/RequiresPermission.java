package com.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限检查注解
 * 用于标记需要权限检查的方法
 *
 * @author Admin
 * @date 2024-01-01
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    /**
     * 需要的权限编码（支持多个，只要有一个即可）
     */
    String[] value() default {};

    /**
     * 需要的角色编码（支持多个，只要有一个即可）
     */
    String[] roles() default {};

    /**
     * 是否要求所有权限（默认false，只要有一个权限即可）
     */
    boolean requireAll() default false;
}





