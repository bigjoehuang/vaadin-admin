# 登录功能配置说明

## 已完成的配置

1. ✅ **创建了 SecurityUserDetailsService**
   - 实现了 Spring Security 的 UserDetailsService 接口
   - 从数据库加载用户信息
   - 验证用户状态（是否删除、是否启用）

2. ✅ **更新了 SecurityConfig**
   - 启用了表单登录（formLogin）
   - 配置了登录处理 URL：`/login`
   - 配置了登录成功后的重定向：`/`（主页）
   - 配置了登录失败后的重定向：`/login?error`

3. ✅ **更新了 LoginView**
   - 如果用户已登录，自动重定向到主页
   - 如果登录失败，显示错误信息

## 默认用户信息

根据数据库迁移脚本 `V2__Insert_default_data.sql`，默认管理员用户信息：

- **用户名**: `admin`
- **密码**: `admin123`
- **邮箱**: `admin@example.com`
- **状态**: 已启用

## 测试登录

1. **启动应用**
   ```bash
   mvn spring-boot:run
   ```
   或在 IntelliJ IDEA 中运行 `AdminApplication`

2. **访问登录页面**
   - 打开浏览器访问：http://localhost:8080
   - 应该自动重定向到：http://localhost:8080/login

3. **输入登录信息**
   - 用户名：`admin`
   - 密码：`admin123`

4. **点击登录**
   - 如果登录成功，应该重定向到主页（仪表盘）
   - 如果登录失败，会显示错误提示并停留在登录页面

## 如果登录失败

### 可能的原因：

1. **密码哈希不匹配**
   - 数据库中的密码哈希可能不正确
   - 需要重新生成密码哈希并更新数据库

2. **用户不存在**
   - 检查数据库是否有 admin 用户
   - 运行 Flyway 迁移脚本确保数据已插入

3. **用户被禁用或删除**
   - 检查 `sys_user` 表中的 `isEnabled` 和 `deleted` 字段

### 解决方案：

#### 方案 1：重新生成密码哈希

创建一个临时测试类来生成新的密码哈希：

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String encoded = encoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后的密码: " + encoded);
    }
}
```

然后更新数据库：
```sql
UPDATE sys_user 
SET password = '<新生成的哈希>' 
WHERE userName = 'admin';
```

#### 方案 2：验证现有密码哈希

运行以下 SQL 查询检查用户信息：
```sql
SELECT userName, password, isEnabled, deleted 
FROM sys_user 
WHERE userName = 'admin';
```

#### 方案 3：重新插入默认用户

如果用户不存在，可以手动插入：
```sql
INSERT INTO sys_user (userName, password, nickname, email, isEnabled, deleted)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ1C', '管理员', 'admin@example.com', 1, 0);
```

## 调试建议

1. **查看应用日志**
   - 检查是否有异常信息
   - 查看 SecurityUserDetailsService 的日志

2. **检查数据库连接**
   - 确认数据库连接正常
   - 确认 Flyway 迁移已执行

3. **检查浏览器控制台**
   - 查看是否有 JavaScript 错误
   - 查看网络请求的响应

4. **启用调试日志**
   在 `application-dev.yml` 中添加：
   ```yaml
   logging:
     level:
       org.springframework.security: DEBUG
       com.admin.config: DEBUG
   ```

## 下一步

登录成功后，你可以：
1. 访问仪表盘（主页）
2. 访问用户管理页面
3. 访问角色管理页面
4. 访问菜单管理页面

## 注意事项

- 默认密码 `admin123` 仅用于开发环境
- 生产环境必须修改默认密码
- 建议实现密码复杂度验证
- 建议实现登录失败次数限制






