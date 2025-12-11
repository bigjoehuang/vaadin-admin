package com.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志实体
 *
 * @author Admin
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLog extends BaseEntity {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * IP 地址
     */
    private String ip;

    /**
     * 位置
     */
    private String location;

    /**
     * 状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;
}





