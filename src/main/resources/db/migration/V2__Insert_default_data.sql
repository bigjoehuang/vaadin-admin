-- 插入默认数据
-- 插入默认管理员用户（密码：admin123）
-- 密码已使用 BCrypt 加密
INSERT INTO sys_user (userName, password, nickname, email, isEnabled, deleted)
VALUES ('admin', '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG', '管理员', 'admin@example.com', 1, 0)
ON DUPLICATE KEY UPDATE password = VALUES(password);

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

