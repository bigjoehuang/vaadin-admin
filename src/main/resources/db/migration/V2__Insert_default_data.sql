-- 插入默认数据
-- 插入默认管理员用户（密码：admin123，需要在前端或服务中加密）
INSERT INTO sys_user (userName, password, nickname, email, isEnabled, deleted)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ1C', '管理员', 'admin@example.com', 1, 0)
ON DUPLICATE KEY UPDATE userName = userName;

-- 插入默认角色
INSERT INTO sys_role (name, code, description, isEnabled, deleted)
VALUES ('超级管理员', 'SUPER_ADMIN', '超级管理员，拥有所有权限', 1, 0)
ON DUPLICATE KEY UPDATE code = code;

INSERT INTO sys_role (name, code, description, isEnabled, deleted)
VALUES ('普通管理员', 'ADMIN', '普通管理员', 1, 0)
ON DUPLICATE KEY UPDATE code = code;

-- 关联管理员用户和超级管理员角色
INSERT INTO sys_user_role (userId, roleId)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.userName = 'admin' AND r.code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE userId = userId;

