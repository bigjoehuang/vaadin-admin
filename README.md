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

## 许可证

MIT License



