# Vaadin 全栈开发手册（第一版）

> 适用于使用 Vaadin Flow + Spring Boot 开发通用后台系统的工程实践指南。

---

## 1. 基础概念与架构

### 1.1 什么是 Vaadin Flow
Vaadin Flow 是一个基于 Java 的服务器端 UI 框架，通过 Java 代码直接构建前端组件，无需编写前端 HTML/JS。浏览器界面通过 WebSocket / HTTP 与服务端同步状态。

- **UI 渲染在浏览器**，但组件状态在服务端维护
- **事件驱动**（按钮点击、表格选择等均在服务端处理）
- **内置路由系统**
- **可与 Spring Boot 深度集成**

### 1.2 前后端一体化模型
Vaadin 采用服务器端组件模型：
- 界面逻辑（View）→ Java 类
- UI 组件 → Java 对象（如 TextField、Grid）
- 数据绑定 → Java Bean
- 路由 → @Route 注解

无需自己写 API、无需编写前端框架，无需管理前后端通信。

---

## 2. 项目结构规范

### 2.1 推荐目录结构（Spring Boot）
```
src/main/java
 └── com.example.app
      ├── views        # Vaadin 视图层
      ├── components   # 自定义组件
      ├── service      # 业务逻辑
      ├── repository   # 数据访问
      └── config       # 配置类
```

### 2.2 View 文件组织
- 一个路由一个 View
- 每个 View 放在 `views/<domain>` 子目录
- 复杂页面可提取出组件放到 components

---

## 3. 路由与导航

### 3.1 基础路由
```
@Route(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout { }
```

### 3.2 动态标题（推荐）
避免使用 `@PageTitle` 动态占位符，改用：
```
public class DashboardView implements HasDynamicTitle {
    @Override
    public String getPageTitle() {
        return I18NUtil.get("page.dashboard");
    }
}
```

---

## 4. 国际化（I18N）

### 4.1 原则
- 不在 UI 内写死文本
- 所有文本使用 `I18NUtil.get(key)` 获取
- 统一定义 `messages_zh_CN.properties` 等资源文件

### 4.2 避免的坑：状态比较
不要用：
```
item.getStatus().equals(I18NUtil.get("status.enabled"));
```
应使用原始枚举：
```
item.getStatus() == Status.ENABLED;
```
I18N 只用于展示，不用于逻辑判断。

---

## 5. 表单与数据绑定

### 5.1 Binder 数据绑定
```
Binder<User> binder = new Binder<>(User.class);
binder.bind(nameField, User::getName, User::setName);
```

### 5.2 校验
```
binder.forField(nameField)
      .asRequired("必填")
      .withValidator(v -> v.length() >= 2, "最少 2 个字符")
      .bind(User::getName, User::setName);
```

---

## 6. Grid 表格最佳实践

### 6.1 基础使用
```
Grid<User> grid = new Grid<>(User.class, false);
```

### 6.2 列配置
```
grid.addColumn(User::getName).setHeader(I18NUtil.get("name"));
```

### 6.3 分页加载
推荐使用 `CallbackDataProvider`。

---

## 7. 对话框与交互

### 7.1 Dialog
```
Dialog dialog = new Dialog();
dialog.add(new Text("确认删除？"));
dialog.open();
```

### 7.2 通知 Notification
```
Notification.show("保存成功");
```

---

## 8. 与 Spring / MyBatis 集成

### 8.1 Service 注入
```
@Autowired
private UserService userService;
```

### 8.2 数据加载
```
List<User> list = userService.findAll();
grid.setItems(list);
```

### 8.3 事务管理
- 使用 Spring 的 @Transactional
- 避免在 UI 事件内写复杂业务逻辑

---

## 9. 权限与安全

### 9.1 使用 Spring Security
- 登录拦截
- 角色控制
- 页面访问控制

### 9.2 UI 上的权限控制
```
if (!SecurityUtil.hasRole("ADMIN")) {
    deleteButton.setVisible(false);
}
```

---

## 10. 前端（可选增强）
Vaadin Flow 虽然全部可用 Java 完成，但也支持前端增强：
- 前端资源：`frontend/`
- 自定义 JS：`@JsModule`
- 使用 Lit / web component

仅在需要动画、图表、特殊控件时使用。

---

## 11. Theme 主题管理

### 11.1 Theme 目录规范
```
frontend/themes/<theme-name>/
```
必须放对，否则会出现找不到主题错误。

### 11.2 自定义样式
```
:host {
  --vaadin-button-color: red;
}
```

---

## 12. 部署与生产优化

### 12.1 构建
```
mvn -Pproduction clean package
```

### 12.2 性能优化
- 开启生产模式
- 使用懒加载 Grid
- 避免页面一次性渲染过多组件

---

## 13. 常见错误与排查

### 13.1 主题找不到
确认：
- `frontend/themes/<name>` 是否存在
- @Theme 注解名是否一致

### 13.2 页面标题不生效
使用 `HasDynamicTitle` 解决。

### 13.3 I18N 比较导致逻辑错误
避免 UI 文本参与业务逻辑。

---

## 14. 建议的工程规范（推荐采纳）
1. 所有文本必须从 I18N 获取
2. 所有页面标题使用 HasDynamicTitle
3. 业务逻辑写在 Service，不在 View 中堆逻辑
4. 表格统一使用懒加载
5. 表单统一使用 Binder
6. 避免状态等逻辑与展示文案耦合

---

## 15. 后续扩展计划（第二版将加入）
- 完整菜单系统
- 接口与页面自动生成规范
- 最佳 UI 组件库整理
- 与前端图表库集成实践
- 通用后台管理模板

---

## 16. 第二版新增内容

### 16.1 通用后台系统架构示例
提供一个完整的基于 Vaadin + Spring Boot + MyBatis 的后台系统架构蓝图，包括：
- 菜单系统（动态权限 + 树形结构）
- 通用 CRUD 页面模板
- 分页查询模板（含条件过滤）
- 数据导出 / 导入规范

### 16.2 完整菜单系统设计
- 菜单实体（id、name、path、role、icon…）
- 角色与菜单关联
- 动态生成侧边菜单的 MainLayout 示例
- 多级菜单渲染与权限控制

### 16.3 页面自动生成规范（适合后台系统）
- 基于注解自动生成：表格列、表单字段、校验
- 通用 CrudPage 模板类
- 示例：UserPage 只需写 20 行代码即可完成

### 16.4 更完善的 I18N 方案
- 自动根据浏览器语言切换
- 用户可手动切换语言
- 动态刷新 UI 文本
- 国际化 Key 命名规范

### 16.5 Grid 懒加载 + 多条件过滤
- 综合示例：分页、排序、多列过滤、搜索条
- 推荐的 DataProvider 模板
- MyBatis + PageHelper 的分页最佳实践

### 16.6 深入 Binder：表单动态校验
- 动态必填字段
- 联动校验（如开始时间 < 结束时间）
- 提交前整体校验

### 16.7 对话框组件化
提供可复用组件：
- ConfirmDialog（确认对话框）
- FormDialog（抽屉式表单）
- EditDialog（可复用增改窗口）

### 16.8 通用 UI 组件库（基于 Vaadin 封装）
- SearchBar（搜索栏组件）
- PaginationBar（分页组件）
- StatusBadge（状态徽章）
- IconButton（带图标的按钮）

### 16.9 Theme 高级实践
- Dark Mode
- CSS Variables 统一管理
- 多主题切换

---

（完）

