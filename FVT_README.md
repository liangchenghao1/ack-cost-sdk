# FVT (Functional Verification Test) 测试说明

本文档说明了如何为各种语言的SDK运行FVT测试。

## 前提条件

1. 已经通过`kubectl proxy`启动了Kubernetes API服务器代理:
   ```bash
   kubectl proxy --address=0.0.0.0 --port=8080 --accept-hosts='^.*'
   ```

2. 确保可以通过`http://127.0.0.1:8080`访问Kubernetes API服务器。

## 目录结构

所有FVT测试现在都位于`fvt/`目录下：
- `fvt/go/` - Go SDK FVT测试
- `fvt/java/` - Java SDK FVT测试
- `fvt/javascript/` - JavaScript SDK FVT测试
- `fvt/python/` - Python SDK FVT测试

## 使用Makefile运行测试（推荐）

项目根目录下提供了Makefile，可以方便地运行FVT测试：

```bash
# 运行所有语言的FVT测试
make fvt

# 运行特定语言的FVT测试
make fvt-go
make fvt-java
make fvt-javascript
make fvt-python

# 检查环境
make check-env

# 清理测试产物
make clean
```

## 手动运行测试

### Go SDK FVT测试

```bash
cd fvt/go
go test -v fvt_test.go
```

或者运行特定的测试用例:

```bash
cd fvt/go
go test -v fvt_test.go -run TestFVT
```

### Java SDK FVT测试

```bash
cd fvt/java
# 编译Java代码
javac FVTTest.java
# 运行测试
java FVTTest
```

### JavaScript SDK FVT测试

```bash
cd fvt/javascript
node fvt_test.js
```

### Python SDK FVT测试

```bash
cd fvt/python
python3 fvt_test.py
```

## 动态获取集群负载测试说明

所有语言的SDK FVT测试都包含动态获取集群中常见工作负载的功能：
- Deployment工作负载
- StatefulSet工作负载
- DaemonSet工作负载

测试会尝试获取这些工作负载的成本数据，并显示示例结果。

## 测试说明

FVT测试主要验证以下功能:

1. **客户端初始化** - 验证能否使用指定的API服务器地址创建客户端
2. **CostV2 API测试** - 验证CostV2服务的基本功能
   - 基本查询功能
   - 带过滤条件的查询
   - 聚合查询
   - 动态获取集群工作负载
3. **Allocation API测试** - 验证Allocation服务的基本功能
   - 基本查询功能
   - 聚合查询
   - 动态获取工作负载分配成本

测试设计为即使在没有实际Kubernetes集群的情况下也能运行（会显示环境相关的错误信息，但不会导致测试完全失败）。

## 注意事项

1. 测试使用`127.0.0.1:8080`作为API服务器地址，这是通过`kubectl proxy`启动的代理地址。
2. 如果没有实际的Kubernetes集群和ACK Metrics Adapter服务，测试会显示连接错误，但这是预期的行为。
3. 实际运行测试时，确保API服务器地址和端口与`kubectl proxy`命令中指定的一致。
4. 测试结果会显示集群中实际存在的工作负载类型和数量，有助于验证SDK与实际环境的交互能力。