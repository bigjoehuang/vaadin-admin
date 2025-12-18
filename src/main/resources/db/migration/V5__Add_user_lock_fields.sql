-- 添加用户锁定相关字段
ALTER TABLE sys_user
ADD COLUMN loginFailCount INT DEFAULT 0 COMMENT '登录失败次数',
ADD COLUMN lastLoginFailTime BIGINT DEFAULT NULL COMMENT '最后登录失败时间',
ADD COLUMN isLocked BIT(1) DEFAULT 0 COMMENT '锁定状态';

-- 更新现有数据的默认值
UPDATE sys_user
SET loginFailCount = 0,
    isLocked = 0
WHERE loginFailCount IS NULL;