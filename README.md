# ACK Cost SDK

阿里云容器服务 ACK 成本 SDK，用于访问 ACK 成本相关 API。

## 支持的语言

- Go
- Java
- JavaScript
- Python

## 快速开始

请参考各语言目录下的 README.md 文件：

- [Go SDK README](go/README.md)
- [Java SDK README](java/README.md)
- [JavaScript SDK README](javascript/README.md)
- [Python SDK README](python/README.md)

## 示例代码

每个语言 SDK 都包含示例代码，展示如何使用 SDK 访问成本 API：

- Go 示例: [go/examples/](go/examples/)
- Java 示例: [java/src/main/java/com/aliyun/container/service/cost/sdk/examples/](java/src/main/java/com/aliyun/container/service/cost/sdk/examples/)
- JavaScript 示例: [javascript/examples/](javascript/examples/)
- Python 示例: [python/examples/](python/examples/)

## FVT 测试

运行 FVT 测试：

```bash
make fvt
```

运行特定语言的 FVT 测试：

```bash
make fvt-go
make fvt-java
make fvt-javascript
make fvt-python
```

## 单元测试

运行单元测试：

```bash
make test
```

运行特定语言的单元测试：

```bash
make test-go
make test-java
make test-javascript
make test-python
```

## 许可证

Apache 2.0 License