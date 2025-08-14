# 常见问题解答 (FAQ)

## 通用问题

### 1. 什么是ACK成本API？

阿里云容器服务Kubernetes版(ACK)提供了三种成本API：
- **Cost API**：用于查询Pod的实时估算成本和资源使用情况（旧版）
- **Cost V2 API**：在Cost API基础上提供更多功能，支持更灵活的时间范围和多维度资源过滤（推荐）
- **Allocation API**：结合账单数据，提供业务分摊账单的费用，用于财务对账

### 2. 这些API有什么区别？

| 特性 | Cost API | Cost V2 API | Allocation API |
|------|----------|-------------|----------------|
| 数据源 | 实时估算 | 实时估算 | 账单数据结合 |
| 时间范围 | 固定单位 | 灵活设置 | 最小24小时 |
| 资源过滤 | 基础维度 | 多维度精细过滤 | 多维度精细过滤 |
| 时间分段 | 不支持 | 支持 | 支持 |
| 数据聚合 | 不支持 | 支持 | 支持 |
| 闲置成本处理 | 不支持 | 支持 | 支持 |
| 成本分摊 | 不支持 | 支持 | 支持 |

### 3. 如何选择合适的API？

- 如果只需要基本的实时成本信息，可以使用Cost API
- 如果需要进行复杂的实时成本分析和趋势分析，推荐使用Cost V2 API
- 如果需要进行财务对账和业务分摊成本查询，应使用Allocation API

## 认证和权限问题

### 4. 使用这些API需要什么权限？

您需要具有访问Kubernetes API Server的适当RBAC权限。具体来说，需要对`ack-metrics-adapter-api-service`服务具有GET权限。

### 5. 如何配置认证信息？

SDK会使用标准的Kubernetes客户端配置方式，包括：
- kubeconfig文件（默认位置`~/.kube/config`）
- 服务账户令牌（在集群内部运行时）
- 环境变量

## SDK使用问题

### 6. SDK支持哪些编程语言？

目前SDK支持以下编程语言：
- Go
- Python
- JavaScript
- Java

更多语言支持正在开发中。

### 7. 如何处理API调用错误？

SDK提供了统一的错误处理机制，所有错误都包含错误码和详细信息。您可以根据错误码进行不同的处理：

```go
// Go示例
if costErr, ok := err.(*cost.Error); ok {
    switch costErr.Code {
    case cost.ErrCodeBadRequest:
        // 处理错误请求
    case cost.ErrCodeUnauthorized:
        // 处理认证错误
    // ...其他错误处理
    }
}
```

### 8. SDK支持异步调用吗？

- **Go**：通过context支持超时和取消
- **Python**：支持async/await异步调用
- **JavaScript**：原生支持Promise和async/await
- **Java**：支持同步和异步调用

### 9. 如何配置重试机制？

所有语言的SDK都支持可配置的重试机制：

```go
// Go示例
config := &cost.Config{
    RetryCount: 3,  // 重试次数
    RetryWait:  1,  // 重试间隔（秒）
}
```

## 性能和最佳实践

### 10. 如何优化API调用性能？

1. 合理使用过滤参数，减少返回数据量
2. 使用聚合参数，避免多次调用
3. 启用重试机制，提高调用稳定性
4. 合理设置超时时间

### 11. 有推荐的分页或批量处理方式吗？

对于大量数据的处理，建议：
1. 使用`step`参数进行时间分段处理
2. 使用`filter`参数进行资源过滤
3. 使用`aggregate`参数进行数据聚合

### 12. 如何处理闲置成本？

Cost V2和Allocation API都支持闲置成本处理：
- `idle`参数：是否展示闲置成本
- `shareIdle`参数：是否将闲置成本分摊到各个资源
- `shareSplit`参数：闲置分摊策略（weighted或even）

## 故障排除

### 13. 遇到"Unauthorized"错误怎么办？

1. 检查Kubernetes认证配置是否正确
2. 确认RBAC权限是否足够
3. 验证服务账户或kubeconfig是否有效

### 14. 遇到"Too Many Requests"错误怎么办？

1. 检查是否触发了API速率限制
2. 增加重试间隔时间
3. 联系阿里云支持提高配额

### 15. 数据返回为空怎么办？

1. 检查时间窗口设置是否正确
2. 验证过滤条件是否准确
3. 确认集群中是否存在相关资源

## 其他问题

### 16. 支持自定义成本权重吗？

Cost V2 API支持自定义资源成本权重。您可以通过修改`ack-alibaba-cloud-metrics-adapter`组件的配置来设置CPU和内存的权重。

### 17. 如何获取帮助？

如果您遇到问题，可以通过以下方式获取帮助：
1. 查阅API文档和SDK文档
2. 查看示例代码
3. 提交GitHub issue
4. 联系阿里云技术支持