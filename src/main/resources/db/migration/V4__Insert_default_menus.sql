-- 插入默认菜单数据
-- 如果 V2 脚本已经执行过，可以使用此脚本补充菜单数据

-- 插入默认菜单
-- 仪表盘
INSERT IGNORE INTO sys_menu (name, path, component, icon, parentId, sort, isEnabled, deleted)
VALUES ('仪表盘', 'dashboard', NULL, 'vaadin:DASHBOARD', NULL, 1, 1, 0);

-- 用户管理
INSERT IGNORE INTO sys_menu (name, path, component, icon, parentId, sort, isEnabled, deleted)
VALUES ('用户管理', 'users', NULL, 'vaadin:USER', NULL, 2, 1, 0);

-- 角色管理
INSERT IGNORE INTO sys_menu (name, path, component, icon, parentId, sort, isEnabled, deleted)
VALUES ('角色管理', 'roles', NULL, 'vaadin:USERS', NULL, 3, 1, 0);

-- 菜单管理
INSERT IGNORE INTO sys_menu (name, path, component, icon, parentId, sort, isEnabled, deleted)
VALUES ('菜单管理', 'menus', NULL, 'vaadin:MENU', NULL, 4, 1, 0);

-- 操作日志
INSERT IGNORE INTO sys_menu (name, path, component, icon, parentId, sort, isEnabled, deleted)
VALUES ('操作日志', 'operation-logs', NULL, 'vaadin:FILE_TEXT', NULL, 5, 1, 0);

-- 插入默认权限（对应菜单路径）
-- 仪表盘权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, isEnabled, deleted)
VALUES ('仪表盘', 'DASHBOARD', 'menu', 'dashboard', NULL, 'vaadin:DASHBOARD', 1, 1, 0)
ON DUPLICATE KEY UPDATE code = code;

-- 用户管理权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, isEnabled, deleted)
VALUES ('用户管理', 'USER_MANAGE', 'menu', 'users', NULL, 'vaadin:USER', 2, 1, 0)
ON DUPLICATE KEY UPDATE code = code;

-- 角色管理权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, isEnabled, deleted)
VALUES ('角色管理', 'ROLE_MANAGE', 'menu', 'roles', NULL, 'vaadin:USERS', 3, 1, 0)
ON DUPLICATE KEY UPDATE code = code;

-- 菜单管理权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, isEnabled, deleted)
VALUES ('菜单管理', 'MENU_MANAGE', 'menu', 'menus', NULL, 'vaadin:MENU', 4, 1, 0)
ON DUPLICATE KEY UPDATE code = code;

-- 操作日志权限
INSERT INTO sys_permission (name, code, type, path, component, icon, sort, isEnabled, deleted)
VALUES ('操作日志', 'OPERATION_LOG', 'menu', 'operation-logs', NULL, 'vaadin:FILE_TEXT', 5, 1, 0)
ON DUPLICATE KEY UPDATE code = code;

-- 关联超级管理员角色和所有权限
INSERT INTO sys_role_permission (roleId, permissionId)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE roleId = roleId;


