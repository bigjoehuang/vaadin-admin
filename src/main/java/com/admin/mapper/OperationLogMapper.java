package com.admin.mapper;

import com.admin.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志 Mapper 接口
 *
 * @author Admin
 * @date 2024-01-01
 */
@Mapper
public interface OperationLogMapper {
    OperationLog selectById(@Param("id") Long id);

    List<OperationLog> selectAll();

    int insert(OperationLog log);
}





