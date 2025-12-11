# UI 事件处理最佳实践

## 1. 核心原则

### 1.1 避免在 UI 事件内写复杂业务逻辑

**原则：UI 层只负责用户交互和界面更新，业务逻辑应该在 Service 层处理。**

#### ❌ 错误示例

```java
// 错误：在事件监听器中写复杂业务逻辑
button.addClickListener(e -> {
    // 复杂的业务逻辑判断
    if (user.getStatus() == Status.ACTIVE && user.getRole().hasPermission("DELETE")) {
        // 数据验证
        if (validateData()) {
            // 业务计算
            int result = calculateSomething();
            // 数据库操作
            userRepository.save(user);
            // 其他业务逻辑...
        }
    }
});
```

#### ✅ 正确示例

```java
// 正确：事件监听器只负责调用 Service 和更新 UI
button.addClickListener(e -> {
    try {
        // 直接调用 Service 层处理业务逻辑
        service.performAction(userId);
        // 更新 UI
        NotificationUtil.showSuccess("操作成功");
        refreshList();
    } catch (BusinessException e) {
        NotificationUtil.showError(e.getMessage());
    }
});
```

## 2. UI 事件处理模式

### 2.1 简单操作模式

对于简单的操作，可以直接在事件监听器中调用 Service：

```java
// ✅ 简单操作：直接调用 Service
refreshButton.addClickListener(e -> {
    performSearch(); // 内部调用 service.pageUsers()
});
```

### 2.2 需要确认的操作模式

对于需要用户确认的操作，分离确认逻辑和业务调用：

```java
// ✅ 正确：分离确认对话框和业务调用
private void performBatchDelete() {
    Set<User> selected = grid.getSelectedItems();
    if (selected.isEmpty()) {
        NotificationUtil.showError("请至少选择一个");
        return;
    }

    // 提取 ID 列表（UI 层职责）
    List<Long> ids = selected.stream()
        .map(User::getId)
        .collect(Collectors.toList());

    // 显示确认对话框（UI 层职责）
    showConfirmDialog(
        "确认删除",
        "确定要删除选中的用户吗？",
        () -> {
            // 确认后调用 Service（业务逻辑在 Service 层）
            try {
                service.batchDeleteUsers(ids);
                NotificationUtil.showSuccess("删除成功");
                refreshList();
            } catch (Exception e) {
                NotificationUtil.showError("删除失败：" + e.getMessage());
            }
        }
    );
}
```

### 2.3 复杂操作模式

对于复杂的操作，创建专门的处理方法：

```java
// ✅ 正确：将复杂逻辑提取到专门的方法
private void handleBatchOperation(BatchOperationType type) {
    // 1. 获取选中项（UI 层）
    Set<User> selected = getSelectedItems();
    if (selected.isEmpty()) {
        showError("请至少选择一个");
        return;
    }

    // 2. 准备数据（UI 层）
    BatchOperationRequest request = prepareBatchRequest(selected, type);

    // 3. 显示确认对话框（UI 层）
    showConfirmDialog(request, () -> {
        // 4. 调用 Service 处理业务逻辑
        executeBatchOperation(request);
    });
}

private void executeBatchOperation(BatchOperationRequest request) {
    try {
        // 业务逻辑在 Service 层
        service.executeBatchOperation(request);
        showSuccess("操作成功");
        refreshList();
    } catch (Exception e) {
        showError("操作失败：" + e.getMessage());
    }
}
```

## 3. UI 层职责划分

### 3.1 UI 层应该做的事情

✅ **UI 层职责：**
- 获取用户输入
- 显示/隐藏组件
- 更新界面状态
- 调用 Service 层方法
- 显示通知和错误信息
- 处理用户交互（点击、输入等）

```java
// ✅ UI 层示例
private void handleSave() {
    // 1. 获取用户输入（UI 层）
    String userName = userNameField.getValue();
    
    // 2. 基本验证（UI 层，简单验证）
    if (userName == null || userName.trim().isEmpty()) {
        NotificationUtil.showError("用户名不能为空");
        return;
    }
    
    // 3. 调用 Service 处理业务逻辑
    try {
        service.saveUser(userName);
        NotificationUtil.showSuccess("保存成功");
        close();
    } catch (BusinessException e) {
        NotificationUtil.showError(e.getMessage());
    }
}
```

### 3.2 UI 层不应该做的事情

❌ **UI 层不应该：**
- 执行复杂的业务规则判断
- 进行数据库查询和操作
- 执行复杂的计算逻辑
- 处理事务管理
- 进行权限判断（应该在 Service 层或 AOP）

```java
// ❌ 错误：在 UI 层写业务逻辑
private void handleSave() {
    // 错误：复杂的业务规则判断
    if (user.getStatus() == Status.ACTIVE && 
        user.getRole().hasPermission("EDIT") &&
        !isWeekend() && 
        checkBusinessHours()) {
        // 错误：数据库操作
        userRepository.save(user);
        // 错误：复杂计算
        calculateAndUpdateStatistics(user);
    }
}
```

## 4. 常见场景处理

### 4.1 表单提交

```java
// ✅ 正确：表单提交
saveButton.addClickListener(e -> {
    // 1. 验证表单（UI 层，简单验证）
    if (!binder.validate().isOk()) {
        NotificationUtil.showError("请检查表单输入");
        return;
    }
    
    // 2. 获取数据（UI 层）
    User user = new User();
    binder.writeBean(user);
    
    // 3. 调用 Service（业务逻辑在 Service 层）
    try {
        if (isEdit) {
            service.updateUser(user);
        } else {
            service.saveUser(user);
        }
        NotificationUtil.showSuccess("保存成功");
        close();
        refreshCallback.run();
    } catch (BusinessException e) {
        NotificationUtil.showError(e.getMessage());
    }
});
```

### 4.2 批量操作

```java
// ✅ 正确：批量操作
private void performBatchDelete() {
    // 1. 获取选中项（UI 层）
    Set<User> selected = grid.getSelectedItems();
    if (selected.isEmpty()) {
        NotificationUtil.showError("请至少选择一个");
        return;
    }
    
    // 2. 准备数据（UI 层）
    List<Long> ids = selected.stream()
        .map(User::getId)
        .collect(Collectors.toList());
    String names = selected.stream()
        .map(User::getUserName)
        .collect(Collectors.joining("、"));
    
    // 3. 显示确认对话框（UI 层）
    ConfirmDialog dialog = new ConfirmDialog();
    dialog.setHeader("确认删除");
    dialog.setText("确定要删除以下用户吗？\n" + names);
    
    // 4. 确认后调用 Service（业务逻辑在 Service 层）
    dialog.addConfirmListener(e -> {
        try {
            service.batchDeleteUsers(ids);
            NotificationUtil.showSuccess("删除成功");
            grid.deselectAll();
            refreshList();
        } catch (Exception ex) {
            NotificationUtil.showError("删除失败：" + ex.getMessage());
        }
    });
    
    dialog.open();
}
```

### 4.3 分页操作

```java
// ✅ 正确：分页操作
nextPageButton.addClickListener(e -> {
    // 1. 计算下一页（UI 层，简单计算）
    int nextPage = currentPage + 1;
    
    // 2. 调用 Service 获取数据（业务逻辑在 Service 层）
    PageResult<User> result = service.pageUsers(nextPage, pageSize, query);
    
    // 3. 更新 UI（UI 层）
    updateGrid(result);
    updatePaginationInfo(result);
});
```

### 4.4 搜索操作

```java
// ✅ 正确：搜索操作
searchButton.addClickListener(e -> {
    // 1. 构建查询条件（UI 层，从 UI 组件获取值）
    UserQueryDTO query = buildQueryFromUI();
    
    // 2. 调用 Service 查询（业务逻辑在 Service 层）
    try {
        PageResult<User> result = service.pageUsers(pageRequest, query);
        updateGrid(result);
    } catch (Exception e) {
        NotificationUtil.showError("查询失败：" + e.getMessage());
    }
});

// UI 层方法：从 UI 组件构建查询条件
private UserQueryDTO buildQueryFromUI() {
    UserQueryDTO query = new UserQueryDTO();
    query.setUserName(userNameField.getValue());
    query.setEmail(emailField.getValue());
    // ... 其他字段
    return query;
}
```

## 5. 工具类和辅助方法

### 5.1 提取可复用的 UI 逻辑

将可复用的 UI 逻辑提取到工具类或辅助方法：

```java
// ✅ 正确：提取 UI 辅助方法
public class DialogUtil {
    public static void showConfirmDialog(
            String title,
            String message,
            Runnable onConfirm) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(title);
        dialog.setText(message);
        dialog.addConfirmListener(e -> onConfirm.run());
        dialog.open();
    }
}

// 使用
DialogUtil.showConfirmDialog(
    "确认删除",
    "确定要删除吗？",
    () -> service.deleteUser(userId)
);
```

### 5.2 提取计算逻辑

将计算逻辑提取到工具类：

```java
// ✅ 正确：提取计算逻辑
public class PaginationUtil {
    public static int calculateTotalPages(long total, int pageSize) {
        return (int) Math.ceil((double) total / pageSize);
    }
    
    public static boolean hasNextPage(int currentPage, int totalPages) {
        return currentPage < totalPages;
    }
    
    public static boolean hasPrevPage(int currentPage) {
        return currentPage > 1;
    }
}

// 使用
int totalPages = PaginationUtil.calculateTotalPages(total, pageSize);
boolean hasNext = PaginationUtil.hasNextPage(currentPage, totalPages);
```

## 6. 检查清单

在编写 UI 事件处理代码时，检查：

- [ ] 事件监听器中是否包含复杂的业务逻辑判断？
- [ ] 是否有数据库操作直接在 UI 层？
- [ ] 是否有复杂的计算逻辑在 UI 层？
- [ ] 是否可以将逻辑提取到 Service 层？
- [ ] 是否可以将可复用的 UI 逻辑提取到工具类？
- [ ] UI 层是否只负责获取输入、调用 Service、更新界面？

## 7. 重构示例

### 重构前 ❌

```java
deleteButton.addClickListener(e -> {
    User user = grid.getSelectedItem();
    if (user == null) {
        NotificationUtil.showError("请选择要删除的用户");
        return;
    }
    
    // 复杂的业务逻辑判断
    if (user.getStatus() == Status.ACTIVE) {
        // 检查权限
        if (!currentUser.hasPermission("DELETE_USER")) {
            NotificationUtil.showError("无权限");
            return;
        }
        
        // 检查关联数据
        long orderCount = orderRepository.countByUserId(user.getId());
        if (orderCount > 0) {
            NotificationUtil.showError("用户有订单，无法删除");
            return;
        }
        
        // 执行删除
        userRepository.delete(user);
        
        // 更新统计
        statisticsService.updateUserCount();
        
        NotificationUtil.showSuccess("删除成功");
        refreshList();
    } else {
        NotificationUtil.showError("只能删除活跃用户");
    }
});
```

### 重构后 ✅

```java
// UI 层：只负责调用 Service
deleteButton.addClickListener(e -> {
    User user = grid.getSelectedItem();
    if (user == null) {
        NotificationUtil.showError("请选择要删除的用户");
        return;
    }
    
    // 显示确认对话框
    DialogUtil.showConfirmDialog(
        "确认删除",
        "确定要删除用户 " + user.getUserName() + " 吗？",
        () -> {
            try {
                // 业务逻辑在 Service 层
                service.deleteUser(user.getId());
                NotificationUtil.showSuccess("删除成功");
                refreshList();
            } catch (BusinessException e) {
                NotificationUtil.showError(e.getMessage());
            }
        }
    );
});

// Service 层：处理业务逻辑
@Service
public class UserServiceImpl implements UserService {
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        
        // 业务规则判断
        if (user.getStatus() != Status.ACTIVE) {
            throw new BusinessException("只能删除活跃用户");
        }
        
        // 权限检查（或使用 AOP）
        if (!hasPermission("DELETE_USER")) {
            throw new BusinessException("无权限");
        }
        
        // 检查关联数据
        long orderCount = orderRepository.countByUserId(userId);
        if (orderCount > 0) {
            throw new BusinessException("用户有订单，无法删除");
        }
        
        // 执行删除
        userRepository.delete(user);
        
        // 更新统计
        statisticsService.updateUserCount();
    }
}
```

## 8. 总结

**核心原则：**
1. ✅ UI 层只负责用户交互和界面更新
2. ✅ 业务逻辑必须在 Service 层处理
3. ✅ 事件监听器应该简洁，只调用 Service 和更新 UI
4. ✅ 复杂的逻辑应该提取到专门的方法或工具类
5. ✅ 保持代码清晰、可维护、可测试

**记住：UI 事件处理应该像"接线员"，只负责接收请求并转发给正确的处理者（Service 层），而不是自己处理业务逻辑！**


