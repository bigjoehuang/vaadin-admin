# Java 热重载配置说明

## 已完成的配置

1. ✅ 已添加 `spring-boot-devtools` 依赖
2. ✅ 已配置 Spring Boot Maven 插件支持 fork 模式
3. ✅ 已配置 DevTools 自动重启和 LiveReload

## IDE 配置（IntelliJ IDEA）

### 方法 1：使用 Spring Boot 运行配置（推荐）

1. 打开 **Run/Debug Configurations**
2. 选择或创建 Spring Boot 运行配置
3. 在 **Configuration** 标签页中：
   - 确保 **Main class** 设置为 `com.admin.AdminApplication`
   - 在 **VM options** 中添加：`-Dspring.devtools.restart.enabled=true`
4. 在 **Build** 标签页中：
   - 勾选 **Build project automatically**
5. 点击 **OK** 保存

### 方法 2：启用自动编译

1. 打开 **Settings/Preferences** (Mac: `Cmd + ,`, Windows/Linux: `Ctrl + Alt + S`)
2. 导航到 **Build, Execution, Deployment** → **Compiler**
3. 勾选 **Build project automatically**
4. 点击 **OK**

### 方法 3：使用 Registry 设置（高级）

1. 按 `Ctrl + Shift + A` (Mac: `Cmd + Shift + A`) 打开 **Find Action**
2. 输入 `Registry` 并选择
3. 找到并勾选 `compiler.automake.allow.when.app.running`
4. 点击 **Close**

## 使用说明

### 自动重启

修改 Java 代码后，DevTools 会自动检测变化并重启应用。重启速度比完全启动快得多。

### LiveReload

- DevTools 会在端口 `35729` 启动 LiveReload 服务器
- 浏览器会自动刷新页面（需要安装 LiveReload 浏览器扩展，或使用 Vaadin 的内置刷新机制）

### 排除的文件/目录

以下目录的更改不会触发重启：
- `static/**`
- `public/**`
- `templates/**`
- `META-INF/maven/**`
- `META-INF/resources/**`

### 手动触发重启

如果需要手动触发重启，可以：
1. 停止应用
2. 重新运行应用
3. 或者修改 `application-dev.yml` 中的任意配置项

## 注意事项

1. **生产环境**：DevTools 只在开发环境生效（`spring.profiles.active=dev`）
2. **性能**：自动重启比完全启动快，但仍需要几秒钟
3. **类加载器**：DevTools 使用两个类加载器，基础类（如第三方库）不会重启，只有你的代码会重启
4. **数据库连接**：重启时数据库连接会重新建立，但 Flyway 不会重复执行已执行的迁移脚本

## 故障排除

### 如果热重载不工作：

1. 检查是否在开发环境（`spring.profiles.active=dev`）
2. 确认 `spring-boot-devtools` 依赖已正确添加
3. 检查 IDE 的自动编译是否启用
4. 查看控制台日志，确认 DevTools 已启动
5. 尝试手动重新编译项目（`Build` → `Rebuild Project`）

### 查看 DevTools 日志

在 `application-dev.yml` 中已启用 DevTools 的 DEBUG 日志：
```yaml
logging:
  level:
    org.springframework.boot.devtools: DEBUG
```

## 参考文档

- [Spring Boot DevTools 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
- [Vaadin 开发模式文档](https://vaadin.com/docs/latest/flow/guide/configuration/dev-mode)


