# 应用连接问题排查指南

## 当前状态

✅ **应用正在运行**
- 端口 8080 正在监听
- 应用可以响应 HTTP 请求
- curl 测试成功返回 HTML 内容

## 可能的问题和解决方案

### 1. 应用在调试模式下暂停

如果应用在调试模式下启动（`suspend=y`），应用会暂停等待调试器连接。

**解决方案：**
- 在 IntelliJ IDEA 中，点击调试工具栏的 **Resume** 按钮（或按 `F9`）
- 或者停止当前运行，使用普通模式重新启动（不是调试模式）

### 2. 浏览器访问问题

**尝试以下方法：**

1. **清除浏览器缓存**
   - Chrome/Edge: `Ctrl+Shift+Delete` (Windows) 或 `Cmd+Shift+Delete` (Mac)
   - 选择"缓存的图片和文件"，清除缓存

2. **使用无痕/隐私模式**
   - 打开浏览器的无痕/隐私模式窗口
   - 访问 http://localhost:8080

3. **检查浏览器控制台**
   - 按 `F12` 打开开发者工具
   - 查看 Console 标签页是否有错误信息
   - 查看 Network 标签页，检查请求是否成功

4. **尝试不同的浏览器**
   - 如果使用 Chrome，尝试 Firefox 或 Safari
   - 如果使用 Edge，尝试 Chrome

### 3. 检查应用日志

查看应用启动日志，确认：
- 应用是否完全启动
- 是否有错误信息
- 端口是否正确（应该是 8080）

### 4. 验证应用可访问性

在终端运行以下命令测试：

```bash
# 测试基本连接
curl -I http://localhost:8080

# 测试登录页面
curl -L http://localhost:8080/login

# 查看应用进程
ps aux | grep AdminApplication
```

### 5. 重启应用

如果以上方法都不行，尝试：

1. **完全停止应用**
   - 在 IntelliJ IDEA 中点击停止按钮
   - 或者使用命令：`kill <PID>`（PID 从 `ps aux | grep AdminApplication` 获取）

2. **清理并重新编译**
   ```bash
   mvn clean compile
   ```

3. **重新启动应用**
   - 在 IntelliJ IDEA 中使用普通运行模式（不是调试模式）
   - 或者使用命令：`mvn spring-boot:run`

### 6. 检查防火墙和网络

- 确保本地防火墙没有阻止 8080 端口
- 确保没有其他应用占用 8080 端口
- 尝试访问 `http://127.0.0.1:8080` 而不是 `http://localhost:8080`

## 正确的访问地址

- **主页（需要登录）**: http://localhost:8080
- **登录页面**: http://localhost:8080/login

## 应用配置

当前配置：
- **端口**: 8080（Spring Boot 默认端口）
- **数据库**: MySQL (localhost:3306)
- **开发模式**: 已启用
- **热重载**: 已配置

## 如果问题仍然存在

1. 检查 IntelliJ IDEA 的运行配置
2. 查看完整的应用启动日志
3. 确认数据库连接正常
4. 检查是否有端口冲突

## 快速测试命令

```bash
# 检查端口是否被占用
lsof -i :8080

# 测试 HTTP 连接
curl -v http://localhost:8080

# 查看应用进程
ps aux | grep java | grep AdminApplication
```

