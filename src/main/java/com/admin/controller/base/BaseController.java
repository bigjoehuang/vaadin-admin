package com.admin.controller.base;

import com.admin.util.Result;

/**
 * 基础控制器
 *
 * @author Admin
 * @date 2024-01-01
 */
public class BaseController {

    /**
     * 成功响应
     */
    protected <T> Result<T> success() {
        return Result.success();
    }

    /**
     * 成功响应（带数据）
     */
    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    /**
     * 失败响应
     */
    protected <T> Result<T> error(String message) {
        return Result.error(message);
    }
}






