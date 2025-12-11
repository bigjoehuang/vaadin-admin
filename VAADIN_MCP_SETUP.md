# Vaadin MCP Server 配置指南

## 什么是 MCP Server？

MCP (Model Context Protocol) 是一个用于 AI 助手与外部工具和数据源集成的协议。配置 Vaadin 官方的 MCP Server 后，AI 助手（如 Cursor）可以直接访问最新的 Vaadin 文档，提供更准确的代码建议和帮助。

## 配置步骤

### 1. 打开 Cursor 设置

在 Cursor IDE 中：
- **macOS**: `Cursor` → `Settings` → `Features` → `MCP`
- **Windows/Linux**: `File` → `Preferences` → `Settings` → `Features` → `MCP`

或者使用快捷键：
- **macOS**: `Cmd + ,`
- **Windows/Linux**: `Ctrl + ,`

然后搜索 "MCP"。

### 2. 添加 Vaadin MCP Server

在 MCP 设置页面，点击 "Add Server" 或 "Edit Config"，添加以下配置：

#### 方式一：使用 SSE (Server-Sent Events) 连接（推荐）

```json
{
  "mcpServers": {
    "vaadin": {
      "command": "npx",
      "args": [
        "-y",
        "@vaadin/mcp-server"
      ],
      "env": {}
    }
  }
}
```

#### 方式二：使用 HTTP 连接

```json
{
  "mcpServers": {
    "vaadin": {
      "type": "http",
      "url": "https://mcp.vaadin.com/docs"
    }
  }
}
```

### 3. 配置文件位置

如果通过 UI 无法配置，可以直接编辑配置文件：

#### macOS
配置文件位置：`~/Library/Application Support/Cursor/User/globalStorage/cursor.mcp/settings.json`

#### Windows
配置文件位置：`%APPDATA%\Cursor\User\globalStorage\cursor.mcp\settings.json`

#### Linux
配置文件位置：`~/.config/Cursor/User/globalStorage/cursor.mcp/settings.json`

### 4. 完整配置示例

```json
{
  "mcpServers": {
    "vaadin": {
      "command": "npx",
      "args": [
        "-y",
        "@vaadin/mcp-server"
      ],
      "env": {
        "NODE_ENV": "production"
      }
    }
  }
}
```

### 5. 重启 Cursor

配置完成后，**必须重启 Cursor IDE** 才能使配置生效。

### 6. 验证配置

重启后，尝试在 Cursor 中询问 Vaadin 相关问题，例如：

- "如何在 Vaadin 中创建按钮？"
- "Vaadin Grid 如何实现分页？"
- "如何在 Vaadin 中使用主题？"

如果配置成功，AI 助手应该能够提供基于最新 Vaadin 文档的准确回答。

## 常见问题

### 1. MCP Server 连接失败

**问题描述：**
- 配置后无法连接 MCP Server
- 提示 "Connection failed" 或 "Server not found"

**解决方案：**
1. **检查网络连接**
   - 确保能够访问互联网
   - 检查防火墙设置

2. **检查 Node.js 环境**
   - 如果使用 `npx` 方式，确保已安装 Node.js
   - 运行 `node --version` 检查版本（建议 16+）

3. **检查配置文件格式**
   - 确保 JSON 格式正确
   - 使用 JSON 验证工具检查语法

4. **查看日志**
   - 在 Cursor 中打开开发者工具（`Help` → `Toggle Developer Tools`）
   - 查看 Console 中的错误信息

### 2. MCP Server 响应慢

**问题描述：**
- MCP Server 响应时间过长
- 查询结果延迟

**解决方案：**
1. **使用本地缓存**
   - 某些 MCP Server 支持本地缓存
   - 检查是否有缓存配置选项

2. **检查网络速度**
   - 使用 HTTP 方式时，网络速度影响响应时间
   - 考虑使用代理或 VPN

3. **使用 SSE 方式**
   - SSE 方式通常比 HTTP 方式更快
   - 尝试切换到 SSE 连接

### 3. 配置不生效

**问题描述：**
- 配置后重启，但 MCP Server 仍未生效
- AI 助手仍然无法访问 Vaadin 文档

**解决方案：**
1. **确认配置文件位置**
   - 检查配置文件路径是否正确
   - 确认使用的是正确的配置文件

2. **检查配置格式**
   - 确保 JSON 格式正确
   - 注意引号和逗号的使用

3. **完全重启 Cursor**
   - 完全退出 Cursor（不是关闭窗口）
   - 重新启动 Cursor

4. **检查 MCP Server 状态**
   - 在 Cursor 设置中查看 MCP Server 状态
   - 确认服务器已连接

### 4. 权限问题

**问题描述：**
- 无法写入配置文件
- 提示权限不足

**解决方案：**
1. **检查文件权限**
   ```bash
   # macOS/Linux
   chmod 644 ~/Library/Application\ Support/Cursor/User/globalStorage/cursor.mcp/settings.json
   ```

2. **使用管理员权限**
   - 在 Windows 上，以管理员身份运行 Cursor
   - 在 macOS/Linux 上，使用 `sudo`（不推荐）

## 官方文档链接

- **Vaadin MCP Server 主页**: https://vaadin.com/docs/latest/building-apps/mcp
- **Cursor 配置指南**: https://vaadin.com/docs/latest/building-apps/mcp/supported-tools/cursor
- **MCP 协议文档**: https://modelcontextprotocol.io

## 其他支持的 AI 工具

Vaadin MCP Server 还支持以下 AI 开发工具：

- **Claude Code**: https://vaadin.com/docs/latest/building-apps/mcp/supported-tools/claude-code
- **Windsurf**: https://vaadin.com/docs/latest/building-apps/mcp/supported-tools/windsurf
- **Junie (JetBrains IDEs)**: https://vaadin.com/docs/latest/building-apps/mcp/supported-tools/junie
- **GitHub Copilot**: https://vaadin.com/docs/latest/building-apps/mcp/supported-tools/github-copilot
- **Codex (OpenAI)**: https://vaadin.com/docs/latest/building-apps/mcp/supported-tools/codex
- **Gemini CLI (Google)**: https://vaadin.com/docs/latest/building-apps/mcp/supported-tools/gemini-cli

## 注意事项

1. **版本兼容性**
   - 确保 Cursor 版本支持 MCP
   - 某些旧版本可能不支持 MCP 功能

2. **网络要求**
   - MCP Server 需要网络连接
   - 某些企业网络可能需要配置代理

3. **性能影响**
   - MCP Server 可能会增加 AI 助手的响应时间
   - 如果不需要，可以禁用 MCP Server

4. **数据隐私**
   - MCP Server 可能会将查询发送到外部服务器
   - 确保符合公司的数据隐私政策

## 更新配置

如果 Vaadin 更新了 MCP Server，可能需要更新配置：

1. 检查 Vaadin 官方文档是否有配置变更
2. 更新配置文件中的 URL 或命令
3. 重启 Cursor 使配置生效

## 禁用 MCP Server

如果需要禁用 MCP Server：

1. 在 Cursor 设置中找到 MCP 配置
2. 删除或注释掉 Vaadin MCP Server 配置
3. 重启 Cursor

或者直接编辑配置文件，删除相关配置项。


