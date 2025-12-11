# 使用 Flyway 更新管理员密码

## 已创建的迁移脚本

已创建新的 Flyway 迁移脚本：`V3__Update_admin_password.sql`

此脚本会：
- 更新 `admin` 用户的密码为 `admin123`（BCrypt 加密）
- 脚本是幂等的，可以安全地重复执行
- 只有在密码不匹配时才会更新

## 方法 1：重启应用（推荐）

最简单的方式是重启应用，Flyway 会自动检测并执行新的迁移脚本：

1. **停止当前运行的应用**

2. **重新启动应用**
   ```bash
   mvn spring-boot:run
   ```
   或在 IntelliJ IDEA 中重新运行 `AdminApplication`

3. **查看启动日志**
   - Flyway 会自动执行 `V3__Update_admin_password.sql`
   - 在日志中会看到类似以下信息：
     ```
     Migrating schema to version 3 - Update admin password
     Successfully applied 1 migration to schema
     ```

## 方法 2：使用 Maven Flyway 插件

如果不想重启应用，可以使用 Maven Flyway 插件手动执行迁移：

```bash
mvn flyway:migrate
```

这会执行所有未执行的迁移脚本。

## 方法 3：验证迁移状态

查看 Flyway 迁移历史：

```bash
mvn flyway:info
```

这会显示所有迁移脚本的执行状态。

## 方法 4：手动执行 SQL（如果 Flyway 不工作）

如果 Flyway 迁移有问题，也可以直接执行 SQL：

```sql
UPDATE sys_user 
SET password = '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG',
    updatedAt = NOW()
WHERE userName = 'admin';
```

## 验证密码更新

执行迁移后，可以验证密码是否已更新：

```sql
SELECT userName, password, updatedAt 
FROM sys_user 
WHERE userName = 'admin';
```

## 登录测试

更新密码后，使用以下凭据登录：
- **用户名**: `admin`
- **密码**: `admin123`

## 故障排查

### 如果迁移脚本没有执行

1. **检查 Flyway 配置**
   - 确认 `application.yml` 中 `spring.flyway.enabled=true`
   - 确认迁移脚本位置正确：`classpath:db/migration`

2. **检查迁移脚本命名**
   - 文件名必须是：`V3__Update_admin_password.sql`
   - 版本号必须递增（V1, V2, V3...）
   - 描述部分使用下划线分隔

3. **检查 Flyway 历史表**
   ```sql
   SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;
   ```
   查看是否已记录 V3 迁移

4. **手动标记迁移为已执行（不推荐）**
   如果迁移脚本已手动执行，可以手动插入记录：
   ```sql
   INSERT INTO flyway_schema_history 
   (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success)
   VALUES 
   (3, '3', 'Update admin password', 'SQL', 'V3__Update_admin_password.sql', 
    <checksum>, 'root', NOW(), 0, 1);
   ```
   **注意**：不推荐手动操作，应该让 Flyway 自动管理

### 如果密码仍然不正确

1. **检查密码哈希**
   - 确认数据库中的密码哈希是否正确
   - 可以使用 `PasswordGenerator` 工具重新生成

2. **检查用户状态**
   ```sql
   SELECT userName, password, isEnabled, deleted 
   FROM sys_user 
   WHERE userName = 'admin';
   ```
   - `isEnabled` 应该是 `1`
   - `deleted` 应该是 `0`

3. **重新生成密码哈希**
   ```bash
   mvn compile exec:java -Dexec.mainClass="com.admin.util.PasswordGenerator" -Dexec.classpathScope=compile
   ```

## 迁移脚本说明

### V3__Update_admin_password.sql

```sql
-- 更新管理员密码
-- 将 admin 用户的密码更新为 admin123（BCrypt 加密）
-- 此脚本是幂等的，可以安全地重复执行

UPDATE sys_user 
SET password = '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG',
    updatedAt = NOW()
WHERE userName = 'admin' 
  AND (password != '$2a$10$seSlmKMS9IE5QQiwvEwxS.4rs/ilXwgaw7jCPxh3zct7I1.VkCZZG' OR password IS NULL);
```

**特点**：
- ✅ 幂等性：可以安全地重复执行
- ✅ 条件更新：只有在密码不匹配时才更新
- ✅ 更新 `updatedAt` 字段

## 下一步

1. 重启应用或执行 `mvn flyway:migrate`
2. 查看应用日志确认迁移成功
3. 使用 `admin` / `admin123` 登录测试



