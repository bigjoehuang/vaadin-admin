package com.admin.service;

import com.admin.entity.OperationLog;

import java.util.List;

/**
 * 操作日志服务接口
 *
 * @author Admin
 * @date 2024-01-01
 */
public interface OperationLogService {
    OperationLog getLogById(Long id);

    List<OperationLog> listLogs();

    void saveLog(OperationLog log);
}


