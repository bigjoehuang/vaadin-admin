package com.admin.aspect;

import com.admin.annotation.LogOperation;
import com.admin.entity.OperationLog;
import com.admin.entity.User;
import com.admin.service.OperationLogService;
import com.admin.util.JsonUtil;
import com.admin.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 *
 * @author Admin
 * @date 2024-01-01
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Pointcut("@annotation(com.admin.annotation.LogOperation)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        OperationLog operationLog = new OperationLog();
        Object result = null;

        try {
            // 获取方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            LogOperation logOperation = method.getAnnotation(LogOperation.class);

            // 设置操作类型和描述
            String operation = logOperation.value();
            if (operation == null || operation.isEmpty()) {
                operation = method.getName();
            }
            String description = logOperation.description();
            if (description == null || description.isEmpty()) {
                description = operation;
            }
            operationLog.setOperation(operation);

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setMethod(request.getMethod());
                operationLog.setIp(getIpAddress(request));
            }

            // 获取用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                operationLog.setUsername(username);
                // 尝试获取用户ID（如果 SecurityUtil 有相关方法）
                try {
                    User currentUser = SecurityUtil.getCurrentUser();
                    if (currentUser != null) {
                        operationLog.setUserId(currentUser.getId());
                    }
                } catch (Exception e) {
                    // 忽略异常
                }
            }

            // 记录请求参数
            if (logOperation.recordParams()) {
                Object[] args = point.getArgs();
                if (args != null && args.length > 0) {
                    try {
                        String params = JsonUtil.toJson(args);
                        // 限制参数长度，避免过长
                        if (params != null && params.length() > 2000) {
                            params = params.substring(0, 2000) + "...";
                        }
                        operationLog.setParams(params);
                    } catch (Exception e) {
                        log.warn("记录操作日志参数失败", e);
                    }
                }
            }

            // 执行方法
            result = point.proceed();

            // 记录返回结果（如果需要）
            if (logOperation.recordResult() && result != null) {
                try {
                    String resultJson = JsonUtil.toJson(result);
                    // 限制结果长度
                    if (resultJson != null && resultJson.length() > 1000) {
                        resultJson = resultJson.substring(0, 1000) + "...";
                    }
                    // 注意：OperationLog 实体可能没有 result 字段，这里先不设置
                } catch (Exception e) {
                    log.warn("记录操作日志结果失败", e);
                }
            }

            operationLog.setStatus(1); // 成功
        } catch (Exception e) {
            operationLog.setStatus(0); // 失败
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 记录操作日志
            try {
                operationLog.setCreatedAt(LocalDateTime.now());
                operationLog.setUpdatedAt(LocalDateTime.now());
                operationLogService.saveLog(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }

        return result;
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

