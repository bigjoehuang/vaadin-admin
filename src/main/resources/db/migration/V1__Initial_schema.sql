-- 初始化数据库表结构
-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    userName VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    isEnabled TINYINT DEFAULT 1 COMMENT '是否启用',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_user_name (userName)
) COMMENT='用户表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    isEnabled TINYINT DEFAULT 1 COMMENT '是否启用',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_role_code (code)
) COMMENT='角色表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL COMMENT '权限编码',
    type VARCHAR(20) COMMENT '权限类型：menu-菜单，button-按钮，api-接口',
    parentId INT UNSIGNED COMMENT '父权限ID',
    path VARCHAR(255) COMMENT '权限路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    isEnabled TINYINT DEFAULT 1 COMMENT '是否启用',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_permission_code (code)
) COMMENT='权限表';

-- 创建菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(255) COMMENT '菜单路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    parentId INT UNSIGNED COMMENT '父菜单ID',
    sort INT DEFAULT 0 COMMENT '排序',
    isEnabled TINYINT DEFAULT 1 COMMENT '是否启用',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除'
) COMMENT='菜单表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    userId INT UNSIGNED NOT NULL COMMENT '用户ID',
    roleId INT UNSIGNED NOT NULL COMMENT '角色ID',
    PRIMARY KEY (userId, roleId),
    KEY idx_user_id (userId),
    KEY idx_role_id (roleId)
) COMMENT='用户角色关联表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    roleId INT UNSIGNED NOT NULL COMMENT '角色ID',
    permissionId INT UNSIGNED NOT NULL COMMENT '权限ID',
    PRIMARY KEY (roleId, permissionId),
    KEY idx_role_id (roleId),
    KEY idx_permission_id (permissionId)
) COMMENT='角色权限关联表';

-- 创建操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    userId INT UNSIGNED COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(50) COMMENT '操作类型',
    method VARCHAR(10) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(255) COMMENT '位置',
    status TINYINT COMMENT '状态：0-失败，1-成功',
    errorMsg TEXT COMMENT '错误信息',
    KEY idx_user_id (userId),
    KEY idx_created_at (createdAt)
) COMMENT='操作日志表';





