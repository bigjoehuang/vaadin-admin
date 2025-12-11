# Vaadin Admin 通用后台管理基础框架

基于 Vaadin + Spring Boot + MyBatis + JDK 21 的通用后台管理基础框架。

## 技术栈

- **Java**: JDK 21
- **Web 框架**: Spring Boot 3.2.x
- **UI 框架**: Vaadin 24.x
- **ORM**: MyBatis 3.0.x
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **安全**: Spring Security
- **数据库版本管理**: Flyway
- **构建工具**: Maven

## 功能特性

- ✅ 权限管理系统（RBAC）
- ✅ 菜单管理系统
- ✅ 代码生成器
- ✅ 文件上传管理（本地/OSS）
- ✅ 数据导出功能（Excel/CSV）
- ✅ 操作日志系统
- ✅ 统一异常处理
- ✅ 工具类库

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 配置数据库

修改 `src/main/resources/application-dev.yml` 中的数据库连接信息。

### 3. 运行项目

```bash
mvn spring-boot:run
```

### 4. 访问系统

打开浏览器访问：http://localhost:8080

## 项目结构

```
vaadin-admin/
├── src/main/java/com/admin/
│   ├── entity/          # 实体类
│   ├── mapper/          # MyBatis Mapper
│   ├── service/         # 服务层
│   ├── controller/     # 控制器
│   ├── views/           # Vaadin 视图
│   ├── component/       # 自定义组件
│   ├── util/            # 工具类
│   ├── config/          # 配置类
│   ├── exception/       # 异常处理
│   └── generator/      # 代码生成器
└── src/main/resources/
    ├── application.yml  # 配置文件
    ├── mapper/          # MyBatis XML
    ├── themes/          # Vaadin 主题
    └── db/migration/    # Flyway 迁移脚本
```

## 开发规范

详见 [.cursorrules](.cursorrules) 文件。

## 常见问题与解决方案

### 1. 样式设置无效问题

**问题描述：**
- CSS 样式修改后不生效
- 组件样式被默认主题覆盖
- 暗色主题下文字不可见

**解决方案：**
1. **使用 CSS 变量而非直接样式**
   ```css
   /* ✅ 正确：使用 CSS 变量 */
   :root {
     --lumo-primary-color: #1976d2;
     --lumo-body-text-color: #1a1a1a;
   }
   
   /* ❌ 错误：直接设置可能被覆盖 */
   vaadin-button {
     color: #1a1a1a;
   }
   ```

2. **使用 `!important` 强制覆盖**
   ```css
   /* 对于必须覆盖的样式 */
   vaadin-button {
     color: var(--lumo-body-text-color) !important;
   }
   ```

3. **使用 `::part()` 选择器**
   ```css
   /* Vaadin 组件内部元素需要使用 part 选择器 */
   vaadin-button::part(label) {
     color: var(--lumo-body-text-color) !important;
   }
   ```

4. **样式文件位置**
   - 必须放在 `src/main/resources/themes/admin-theme/styles.css`
   - 确保 `@Theme("admin-theme")` 注解与主题名一致

5. **清除浏览器缓存**
   - 开发模式下样式可能被缓存，清除浏览器缓存或使用无痕模式

### 2. 多语言切换问题

**问题描述：**
- 使用 I18N 文本进行状态比较导致逻辑错误
- 切换语言后筛选条件失效
- 状态判断失败

**解决方案：**
1. **绝对禁止使用 I18N 文本进行状态比较**
   ```java
   // ❌ 错误：使用 I18N 文本比较
   if (status.equals(I18NUtil.get("status.enabled"))) {
       // 切换语言后会失败！
   }
   
   // ✅ 正确：使用常量或枚举
   if (StatusConstant.ENABLED.equals(status)) {
       // 逻辑判断使用原始值
   }
   ```

2. **筛选器使用常量值，显示 I18N 文本**
   ```java
   ComboBox<String> statusFilter = new ComboBox<>();
   statusFilter.setItems(StatusConstant.ALL, StatusConstant.ENABLED);
   statusFilter.setItemLabelGenerator(status -> {
       if (StatusConstant.ENABLED.equals(status)) {
           return I18NUtil.get("status.enabled");
       }
       return status;
   });
   ```

3. **核心原则**
   - I18N 只用于展示，不用于逻辑判断
   - 状态比较使用常量或枚举
   - 筛选器值使用常量，显示文本使用 I18N

**详细说明：** 详见 [I18N_USAGE_GUIDE.md](I18N_USAGE_GUIDE.md)

### 3. Flyway 迁移问题

**问题描述：**
- 迁移脚本不执行
- 密码哈希不匹配导致登录失败
- 迁移脚本命名错误

**解决方案：**
1. **迁移脚本命名规范**
   - 版本迁移：`V{version}__{description}.sql`（如 `V3__Update_admin_password.sql`）
   - 可重复迁移：`R__{description}.sql`
   - 版本号必须递增，不能跳过

2. **密码哈希问题**
   ```java
   // 生成密码哈希
   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
   String encoded = encoder.encode("admin123");
   ```
   
   然后通过 Flyway 迁移脚本更新：
   ```sql
   UPDATE sys_user 
   SET password = '$2a$10$...',
       updatedAt = NOW()
   WHERE userName = 'admin';
   ```

3. **迁移脚本幂等性**
   ```sql
   -- ✅ 正确：幂等性脚本
   UPDATE sys_user 
   SET password = '$2a$10$...'
   WHERE userName = 'admin' 
     AND (password != '$2a$10$...' OR password IS NULL);
   ```

4. **检查迁移状态**
   ```bash
   # 查看迁移历史
   mvn flyway:info
   
   # 手动执行迁移
   mvn flyway:migrate
   ```

5. **常见错误**
   - 文件名格式错误（必须双下划线 `__`）
   - 版本号不递增
   - 脚本位置错误（必须在 `classpath:db/migration`）

**详细说明：** 详见 [FLYWAY_PASSWORD_UPDATE.md](FLYWAY_PASSWORD_UPDATE.md)

### 4. 登录功能问题

**问题描述：**
- 密码正确但登录失败
- 用户状态检查失败
- 密码哈希不匹配

**解决方案：**
1. **检查用户状态**
   ```sql
   SELECT userName, password, isEnabled, deleted 
   FROM sys_user 
   WHERE userName = 'admin';
   ```
   - `isEnabled` 必须为 `1`
   - `deleted` 必须为 `0`

2. **验证密码哈希**
   - 使用 `PasswordGenerator` 工具重新生成
   - 确保使用 BCrypt 加密
   - 通过 Flyway 迁移脚本更新

3. **默认登录信息**
   - 用户名：`admin`
   - 密码：`admin123`
   - 仅用于开发环境

4. **启用调试日志**
   ```yaml
   logging:
     level:
       org.springframework.security: DEBUG
       com.admin.config: DEBUG
   ```

**详细说明：** 详见 [LOGIN_SETUP.md](LOGIN_SETUP.md)

### 5. 热重载不工作

**问题描述：**
- 修改代码后不自动重启
- IDE 自动编译未启用

**解决方案：**
1. **IntelliJ IDEA 配置**
   - Settings → Build → Compiler → 勾选 "Build project automatically"
   - Registry → 勾选 `compiler.automake.allow.when.app.running`

2. **运行配置**
   - VM options 添加：`-Dspring.devtools.restart.enabled=true`
   - 确保使用开发环境：`spring.profiles.active=dev`

3. **检查 DevTools 依赖**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

4. **手动触发重启**
   - 修改 `application-dev.yml` 任意配置项
   - 或手动停止并重新运行

**详细说明：** 详见 [LIVE_RELOAD_SETUP.md](LIVE_RELOAD_SETUP.md)

### 6. 应用无法访问

**问题描述：**
- 应用启动但浏览器无法访问
- 端口被占用
- 调试模式暂停

**解决方案：**
1. **检查调试模式**
   - 如果使用调试模式启动，点击 Resume（F9）继续
   - 或使用普通运行模式

2. **清除浏览器缓存**
   - Chrome: `Ctrl+Shift+Delete` (Windows) 或 `Cmd+Shift+Delete` (Mac)
   - 或使用无痕/隐私模式

3. **检查端口占用**
   ```bash
   # 检查端口
   lsof -i :8080
   
   # 测试连接
   curl -v http://localhost:8080
   ```

4. **验证应用状态**
   ```bash
   # 查看进程
   ps aux | grep AdminApplication
   
   # 测试登录页面
   curl -L http://localhost:8080/login
   ```

**详细说明：** 详见 [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

### 7. 数据库连接问题

**问题描述：**
- 连接失败
- 时区错误
- 字符编码问题

**解决方案：**
1. **检查连接配置**
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/vaadin?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
       username: root
       password: root@local
   ```

2. **时区设置**
   - URL 中添加 `serverTimezone=Asia/Shanghai`
   - 或使用 UTC 时区

3. **字符编码**
   - 确保使用 `useUnicode=true&characterEncoding=utf8`
   - 数据库字符集为 `utf8mb4`

### 8. Vaadin 主题问题

**问题描述：**
- 主题找不到
- 样式不生效
- 主题切换失败

**解决方案：**
1. **主题目录位置**
   - 必须放在 `src/main/resources/themes/admin-theme/`
   - 确保 `@Theme("admin-theme")` 注解与目录名一致

2. **主题文件结构**
   ```
   themes/admin-theme/
   ├── styles.css
   ├── theme.json
   └── ...
   ```

3. **清除缓存**
   - 删除 `target/` 目录
   - 重新编译：`mvn clean compile`

## 开发注意事项

### 重要提醒

1. **不要修改已执行的 Flyway 迁移脚本**
   - 已执行的脚本不能修改
   - 如需修改，创建新的迁移脚本

2. **I18N 文本不能用于逻辑判断**
   - 状态比较使用常量或枚举
   - I18N 只用于展示

3. **样式使用 CSS 变量**
   - 优先使用 Lumo CSS 变量
   - 需要覆盖时使用 `!important` 和 `::part()` 选择器

4. **数据库表结构规范**
   - 所有表必须包含 `id`、`createdAt`、`updatedAt` 三个字段
   - 使用 Flyway 管理表结构变更

5. **开发环境配置**
   - 使用 `application-dev.yml` 配置开发环境
   - 生产环境使用 `application-prod.yml`

## 相关文档

- [开发规范](.cursorrules) - 详细的代码规范和开发指南
- [登录配置](LOGIN_SETUP.md) - 登录功能配置说明
- [Flyway 使用](FLYWAY_PASSWORD_UPDATE.md) - 数据库迁移脚本使用指南
- [热重载配置](LIVE_RELOAD_SETUP.md) - Java 热重载配置说明
- [国际化使用](I18N_USAGE_GUIDE.md) - I18N 使用规范和注意事项
- [问题排查](TROUBLESHOOTING.md) - 应用连接问题排查指南

## 许可证

MIT License



