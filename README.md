# Backend Example

后端项目示例，使用 C# (.NET)、Java、Python 三种语言实现相同的管理系统功能，便于对比学习不同技术栈的后端开发范式。

## 架构设计

- **DDD (领域驱动设计)**：以领域模型为核心，划分领域层、应用层、适配器层（主动、被动）
- **CQRS (命令查询职责分离)**：应用服务由 Service 处理，查询由 QueryHandler、QueryCriteria、QuerySort 处理
- **整洁架构 / 六边形架构**：通过 Adapters 实现内外适配器的解耦

详细架构图见 [arch.drawio](./arch.drawio)

## 语言实现

| 特性           | Python            | Java                      | .NET                            |
| -------------- | ----------------- | ------------------------- | ------------------------------- |
| **框架**       | FastAPI + Uvicorn | Spring Boot 3.5           | .NET 9.0 (ASP.NET Core)         |
| **数据库访问** | aiosqlite         | sqlite-jdbc + Spring JDBC | Microsoft.Data.Sqlite + Dapper  |
| **锁实现**     | asyncio \| redis  | reentrant \| redisson     | semaphore \| redis              |
| **认证**       | python-jose (JWT) | java-jwt (JWT)            | System.IdentityModel.Tokens.Jwt |
| **DI 容器**    | 自研 IocContainer | Spring IoC                | .NET DI                         |
| **包管理**     | uv                | Maven                     | NuGet                           |

## 目录结构

```
├── backend-example-dotnet/    # .NET 实现
│   └── src/
│       ├── adapter/driven/    # 被动适配器：持久化适配器、外部API接口适配器
│       ├── adapter/driving/   # 主动适配器：Controller、结果封装、辅助工具
│       ├── application/       # 应用层：应用服务、查询处理、锁服务
│       ├── domain/            # 领域层：领域实体、规约、仓储接口、外部API接口
│       └── utility/           # 工具类
│       └── Program.cs         # .NET 入口
│
├── backend-example-java/      # Java 实现
│   └── src/main/java/com/example/backend/
│       ├── adapter/driven/    # 被动适配器：持久化适配器、外部API接口适配器
│       ├── adapter/driving/   # 主动适配器：Controller、结果封装、辅助工具
│       ├── application/       # 应用层：应用服务、查询处理、锁服务
│       ├── domain/            # 领域层：领域实体、规约、仓储接口、外部API接口
│       ├── utility/           # 工具类
│       └── Application.java   # Spring Boot 入口
│
└── backend-example-python/    # Python 实现
    └── src/
        ├── adapter/driven/    # 被动适配器：持久化适配器、外部API接口适配器
        ├── adapter/driving/   # 主动适配器：Controller、结果封装、辅助工具
        ├── application/       # 应用层：应用服务、查询处理、锁服务
        ├── domain/            # 领域层：领域实体、规约、仓储接口、外部API接口
        ├── utility/           # 工具类
        ├── ioc_container.py   # 依赖注入容器
        └── main.py            # FastAPI 入口
```

## 主动适配器提供的 API 接口

### 用户接口 (需登录)

| 方法 | 路径                         | 说明           |
| ---- | ---------------------------- | -------------- |
| POST | `/api/user/register`         | 注册用户       |
| POST | `/api/user/login`            | 用户登录       |
| GET  | `/api/user/profile`          | 获取用户资料   |
| POST | `/api/user/modify-password`  | 修改密码       |
| POST | `/api/user/modify-nickname`  | 修改昵称       |
| POST | `/api/user/send-mobile-code` | 发送手机验证码 |
| POST | `/api/user/bind-mobile`      | 绑定手机号     |

### 管理员接口 (需管理员权限)

| 方法 | 路径                     | 说明             |
| ---- | ------------------------ | ---------------- |
| POST | `/api/admin/user/list`   | 用户列表（分页） |
| GET  | `/api/admin/user/get`    | 用户详情         |
| POST | `/api/admin/user/create` | 创建用户         |
| POST | `/api/admin/user/modify` | 修改用户         |
| POST | `/api/admin/user/remove` | 删除用户         |

### 统一响应格式

```json
{
  "success": true,
  "code": 0,
  "message": "操作成功",
  "data": { ... }
}
```

## 快速开始

### .NET

```bash
cd backend-example-dotnet
dotnet run
```

### Java

```bash
cd backend-example-java
./mvnw.cmd spring-boot:run
```

### Python

```bash
cd backend-example-python
uv sync
uv run uvicorn src.main:app --host 0.0.0.0 --port 8000 --reload
```

## 测试

每个子项目的 `test/http/` 目录下提供 `.http` 测试文件。
- .NET 项目可通过 Visual Studio 运行
- Java 项目可通过 IDEA 运行
- Python 项目可通过 PyCharm 运行。

## 配置

- .NET 通过 `appsetting.json` 文件配置
- Java 通过 `src/main/resources/application.yaml` 文件配置
- Python 通过 `.env` 文件配置
