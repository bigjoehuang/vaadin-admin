# 国际化（I18N）使用规范

## 1. 基本原则

### 1.1 不在 UI 内写死文本
- **禁止**：在 UI 组件中直接使用硬编码的中文或英文文本
- **正确**：所有文本必须使用 `I18NUtil.get(key)` 获取

### 1.2 统一定义资源文件
- 中文资源文件：`src/main/resources/i18n/messages_zh_CN.properties`
- 英文资源文件：`src/main/resources/i18n/messages_en_US.properties`
- 所有国际化 key 必须同时存在于两个文件中

## 2. 使用方式

### 2.1 基本用法

```java
// ✅ 正确：使用 I18NUtil.get()
Button button = new Button(I18NUtil.get("common.add"));

// ❌ 错误：硬编码文本
Button button = new Button("添加");
```

### 2.2 带参数的用法

```java
// ✅ 正确：使用参数
String message = I18NUtil.get("user.delete.confirm", userName);

// 资源文件定义：
// user.delete.confirm=确定要删除用户 \"{0}\" 吗？此操作不可恢复。
```

### 2.3 在组件中使用

```java
// ✅ 正确示例
TextField userNameField = new TextField(I18NUtil.get("user.userName"));
userNameField.setPlaceholder(I18NUtil.get("user.placeholder.userName"));

Label title = new Label(I18NUtil.get("user.title"));
NotificationUtil.showSuccess(I18NUtil.get("user.save.success"));
```

## 3. 重要原则：状态比较

### 3.1 错误做法 ❌

**绝对禁止使用 I18N 文本进行状态比较！**

```java
// ❌ 错误：使用 I18N 文本比较
if (item.getStatus().equals(I18NUtil.get("status.enabled"))) {
    // ...
}

// ❌ 错误：在筛选器中使用 I18N 文本作为值
ComboBox<String> statusFilter = new ComboBox<>();
statusFilter.setItems(
    I18NUtil.get("common.all"),
    I18NUtil.get("status.enabled"),
    I18NUtil.get("status.disabled")
);
String status = statusFilter.getValue();
if (I18NUtil.get("status.enabled").equals(status)) { // 错误！
    // ...
}
```

**为什么错误？**
- I18N 文本会根据语言变化（中文/英文），导致比较失败
- 逻辑判断应该使用原始值，而不是显示文本
- 切换语言后，原有的筛选条件会失效

### 3.2 正确做法 ✅

**使用常量或枚举进行状态比较**

#### 方法 1：使用常量类（推荐）

```java
// 1. 定义状态常量类
public class StatusConstant {
    public static final String ALL = "ALL";
    public static final String ENABLED = "ENABLED";
    public static final String DISABLED = "DISABLED";
}

// 2. 在筛选器中使用常量值，但显示 I18N 文本
ComboBox<String> statusFilter = new ComboBox<>(I18NUtil.get("user.status"));
statusFilter.setItems(
    StatusConstant.ALL,
    StatusConstant.ENABLED,
    StatusConstant.DISABLED
);
// 设置显示文本生成器
statusFilter.setItemLabelGenerator(status -> {
    if (StatusConstant.ALL.equals(status)) {
        return I18NUtil.get("common.all");
    } else if (StatusConstant.ENABLED.equals(status)) {
        return I18NUtil.get("user.enabled");
    } else if (StatusConstant.DISABLED.equals(status)) {
        return I18NUtil.get("user.disabled");
    }
    return status;
});
statusFilter.setValue(StatusConstant.ALL);

// 3. 在逻辑判断中使用常量值
String status = statusFilter.getValue();
if (StatusConstant.ENABLED.equals(status)) { // ✅ 正确
    currentQuery.setIsEnabled(true);
}
```

#### 方法 2：使用枚举

```java
// 1. 定义状态枚举
public enum Status {
    ENABLED,
    DISABLED
}

// 2. 在逻辑判断中使用枚举
if (item.getStatus() == Status.ENABLED) { // ✅ 正确
    // ...
}
```

## 4. 资源文件规范

### 4.1 Key 命名规范

使用点分隔的层级结构：

```
模块.功能.类型
```

示例：
- `common.add` - 通用添加按钮
- `user.title` - 用户管理标题
- `user.placeholder.userName` - 用户名字段占位符
- `user.validation.userName.required` - 用户名必填验证消息
- `user.save.success` - 保存成功消息

### 4.2 分类组织

资源文件按功能模块分类，使用注释标识：

```properties
# 通用
common.add=添加
common.edit=编辑
common.delete=删除

# 用户管理
user.title=用户管理
user.add=添加用户
user.userName=用户名

# 验证消息
user.validation.userName.required=用户名不能为空
```

### 4.3 参数占位符

使用 `{0}`, `{1}`, `{2}` 等作为参数占位符：

```properties
# 资源文件
user.delete.confirm=确定要删除用户 \"{0}\" 吗？此操作不可恢复。
user.batch.delete.success=批量删除成功，共删除 {0} 个用户

# Java 代码
String message = I18NUtil.get("user.delete.confirm", userName);
String message = I18NUtil.get("user.batch.delete.success", count);
```

## 5. 常见场景

### 5.1 按钮文本

```java
Button addButton = new Button(I18NUtil.get("common.add"));
Button saveButton = new Button(I18NUtil.get("common.save"));
Button cancelButton = new Button(I18NUtil.get("common.cancel"));
```

### 5.2 表单字段

```java
TextField userNameField = new TextField(I18NUtil.get("user.userName"));
userNameField.setPlaceholder(I18NUtil.get("user.placeholder.userName"));
userNameField.setHelperText(I18NUtil.get("user.helper.userName.edit"));
```

### 5.3 通知消息

```java
NotificationUtil.showSuccess(I18NUtil.get("user.save.success"));
NotificationUtil.showError(I18NUtil.get("user.delete.failed", e.getMessage()));
```

### 5.4 确认对话框

```java
ConfirmDialog confirmDialog = new ConfirmDialog();
confirmDialog.setHeader(I18NUtil.get("confirm.delete.title"));
confirmDialog.setText(I18NUtil.get("user.delete.confirm", userName));
confirmDialog.setConfirmText(I18NUtil.get("common.delete"));
confirmDialog.setCancelText(I18NUtil.get("common.cancel"));
```

### 5.5 Grid 列标题

```java
grid.addColumn(User::getUserName)
    .setHeader(I18NUtil.get("user.userName"));
grid.addColumn(User::getEmail)
    .setHeader(I18NUtil.get("user.email"));
```

## 6. 检查清单

在提交代码前，请检查：

- [ ] 所有 UI 文本都使用 `I18NUtil.get()` 获取
- [ ] 没有硬编码的中文或英文文本
- [ ] 状态比较使用常量或枚举，而不是 I18N 文本
- [ ] 所有新增的 key 都已添加到中英文资源文件
- [ ] 资源文件中的 key 命名符合规范
- [ ] 参数占位符使用正确（{0}, {1}, {2}...）

## 7. 示例对比

### 错误示例 ❌

```java
// 硬编码文本
Button button = new Button("添加用户");

// 使用 I18N 文本比较
if (status.equals(I18NUtil.get("status.enabled"))) {
    // ...
}

// 筛选器使用 I18N 文本作为值
ComboBox<String> filter = new ComboBox<>();
filter.setItems(I18NUtil.get("common.all"), I18NUtil.get("status.enabled"));
String value = filter.getValue();
if (I18NUtil.get("status.enabled").equals(value)) { // 错误！
    // ...
}
```

### 正确示例 ✅

```java
// 使用 I18NUtil.get()
Button button = new Button(I18NUtil.get("user.add"));

// 使用枚举比较
if (status == Status.ENABLED) {
    // ...
}

// 筛选器使用常量值，显示 I18N 文本
ComboBox<String> filter = new ComboBox<>(I18NUtil.get("user.status"));
filter.setItems(StatusConstant.ALL, StatusConstant.ENABLED, StatusConstant.DISABLED);
filter.setItemLabelGenerator(status -> {
    if (StatusConstant.ALL.equals(status)) {
        return I18NUtil.get("common.all");
    } else if (StatusConstant.ENABLED.equals(status)) {
        return I18NUtil.get("user.enabled");
    }
    return status;
});
String value = filter.getValue();
if (StatusConstant.ENABLED.equals(value)) { // 正确！
    // ...
}
```

## 8. 总结

**核心原则：**
1. ✅ 所有 UI 文本使用 `I18NUtil.get(key)` 获取
2. ✅ 状态比较使用常量或枚举，**绝不使用 I18N 文本**
3. ✅ I18N 只用于展示，不用于逻辑判断
4. ✅ 资源文件统一管理，key 命名规范

**记住：I18N 文本会根据语言变化，不能用于逻辑判断！**

