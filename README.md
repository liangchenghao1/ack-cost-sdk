# ACK Cost SDK

阿里云容器服务 Kubernetes 版(ACK)成本API的多语言SDK，支持访问Cost API、Cost V2 API和Allocation API。

## 功能特性

- 支持所有三种成本API:
  - Cost API (旧版)
  - Cost V2 API (推荐)
  - Allocation API (业务分摊账单)
- 多语言支持 (Go, Python, JavaScript, Java)
- 同步和异步调用
- 自动重试机制
- 详细的错误处理
- 符合各语言社区最佳实践

## 支持的语言

- [Go SDK](./go/README.md)
- [Python SDK](./python/README.md)
- [JavaScript SDK](./javascript/README.md)
- [Java SDK](./java/README.md)

## 安装

请参考各语言SDK目录中的README文件获取安装说明。

## 快速开始

请参考各语言SDK目录中的示例代码。

## 文档

- [API参考文档](./API.md)
- [快速入门指南](./QUICK_START.md)
- [常见问题解答](./FAQ.md)

## 项目结构

```
cost-sdk/
├── go/                 # Go语言SDK实现
│   ├── examples/       # Go示例代码
│   ├── README.md       # Go SDK使用说明
│   ├── client.go       # 核心客户端实现
│   ├── cost.go         # Cost API实现
│   ├── costv2.go       # Cost V2 API实现
│   ├── allocation.go   # Allocation API实现
│   └── error.go        # 错误处理实现
├── python/             # Python语言SDK实现
│   ├── examples/       # Python示例代码
│   └── README.md       # Python SDK使用说明
├── javascript/         # JavaScript语言SDK实现
│   ├── examples/       # JavaScript示例代码
│   └── README.md       # JavaScript SDK使用说明
├── java/               # Java语言SDK实现
│   ├── examples/       # Java示例代码
│   └── README.md       # Java SDK使用说明
├── API.md              # API参考文档
├── QUICK_START.md      # 快速入门指南
├── FAQ.md              # 常见问题解答
└── README.md           # 项目主说明文件
```

## 核心功能

### 请求处理
- 支持同步和异步调用
- 实现自动重试机制(可配置)
- 处理分页和速率限制

### 错误处理
- 提供清晰的错误分类和异常处理
- 包含详细的错误码说明
- 为每种语言实现符合该语言习惯的异常处理方式

## 代码质量保证

- 代码结构清晰，遵循各语言社区公认的最佳实践
- 包含完整的单元测试和集成测试用例
- 提供实际应用场景的示例代码
- 文档完善，包含版本迁移和兼容性说明
- 依赖管理明确，避免版本冲突

## 许可证

Apache 2.0 License