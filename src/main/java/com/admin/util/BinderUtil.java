package com.admin.util;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;

import java.util.function.Supplier;

/**
 * Binder 工具类
 * 提供动态校验、联动校验等功能
 *
 * @author Admin
 * @date 2024-01-01
 */
public class BinderUtil {

    /**
     * 创建动态必填验证器
     * 根据条件决定字段是否必填
     *
     * @param conditionSupplier 条件提供者，返回 true 表示必填
     * @param errorMessage 错误消息
     * @param <T> 字段类型
     * @return Validator
     */
    public static <T> Validator<T> createDynamicRequiredValidator(
            Supplier<Boolean> conditionSupplier,
            String errorMessage) {
        return (value, context) -> {
            if (conditionSupplier.get()) {
                // 条件为 true，字段必填
                if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                    return ValidationResult.error(errorMessage);
                }
            }
            return ValidationResult.ok();
        };
    }

    /**
     * 创建联动验证器
     * 例如：开始时间必须小于结束时间
     *
     * @param otherValueSupplier 另一个字段的值提供者
     * @param comparator 比较函数，返回 true 表示验证通过
     * @param errorMessage 错误消息
     * @param <T> 字段类型
     * @return Validator
     */
    public static <T> Validator<T> createLinkedValidator(
            Supplier<T> otherValueSupplier,
            java.util.function.BiFunction<T, T, Boolean> comparator,
            String errorMessage) {
        return (value, context) -> {
            T otherValue = otherValueSupplier.get();
            if (value != null && otherValue != null) {
                if (!comparator.apply(value, otherValue)) {
                    return ValidationResult.error(errorMessage);
                }
            }
            return ValidationResult.ok();
        };
    }

    /**
     * 创建条件验证器
     * 根据条件决定是否应用验证规则
     *
     * @param conditionSupplier 条件提供者
     * @param validator 验证器
     * @param <T> 字段类型
     * @return Validator
     */
    public static <T> Validator<T> createConditionalValidator(
            Supplier<Boolean> conditionSupplier,
            Validator<T> validator) {
        return (value, context) -> {
            if (conditionSupplier.get()) {
                return validator.apply(value, context);
            }
            return ValidationResult.ok();
        };
    }
}

