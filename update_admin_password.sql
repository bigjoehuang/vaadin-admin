-- 更新管理员密码为 admin123
-- 此脚本用于更新现有数据库中的管理员密码
-- 如果数据库已经存在 admin 用户，运行此脚本更新密码

-- 方法 1: 使用 UPDATE 语句（推荐）
UPDATE sys_user 
SET password = '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG' 
WHERE userName = 'admin';

-- 方法 2: 使用 INSERT ... ON DUPLICATE KEY UPDATE
INSERT INTO sys_user (userName, password, nickname, email, isEnabled, deleted)
VALUES ('admin', '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG', '管理员', 'admin@example.com', 1, 0)
ON DUPLICATE KEY UPDATE password = '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG';

-- 验证更新结果
SELECT userName, password, nickname, email, isEnabled, deleted 
FROM sys_user 
WHERE userName = 'admin';






