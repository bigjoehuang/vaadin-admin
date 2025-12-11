-- 更新管理员密码
-- 将 admin 用户的密码更新为 admin123（BCrypt 加密）
-- 此脚本是幂等的，可以安全地重复执行

UPDATE sys_user 
SET password = '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG',
    updatedAt = NOW()
WHERE userName = 'admin' 
  AND (password != '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG' OR password IS NULL);





