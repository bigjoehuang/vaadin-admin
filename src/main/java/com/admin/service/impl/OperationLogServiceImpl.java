package com.admin.service.impl;

import com.admin.entity.OperationLog;
import com.admin.mapper.OperationLogMapper;
import com.admin.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志服务实现
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public OperationLog getLogById(Long id) {
        return operationLogMapper.selectById(id);
    }

    @Override
    public List<OperationLog> listLogs() {
        return operationLogMapper.selectAll();
    }

    @Override
    public void saveLog(OperationLog log) {
        operationLogMapper.insert(log);
    }
}






