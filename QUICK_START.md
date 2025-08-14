# 快速入门指南

本文档将指导您如何快速开始使用ACK Cost SDK。

## 1. 安装SDK

### Go

```bash
go get github.com/AliyunContainerService/cost-sdk/go
```

### Python

```bash
pip install ack-cost-sdk
```

### JavaScript

```bash
npm install ack-cost-sdk
```

### Java

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.aliyun.container.service</groupId>
    <artifactId>ack-cost-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 2. 配置客户端

### Go

```go
client, err := cost.NewClient(&cost.Config{
    APIServer:  "https://kubernetes.default.svc",
    RetryCount: 3,
    RetryWait:  2,
})
```

### Python

```python
from ack_cost_sdk import Client, Config

client = Client(Config(
    api_server="https://kubernetes.default.svc",
    retry_count=3,
    retry_wait=2
))
```

### JavaScript

```javascript
const { Client, Config } = require('ack-cost-sdk');

const client = new Client(new Config({
    apiServer: "https://kubernetes.default.svc",
    retryCount: 3,
    retryWait: 2
}));
```

### Java

```java
import com.aliyun.container.service.cost.sdk.Client;
import com.aliyun.container.service.cost.sdk.Config;

Config config = new Config()
    .setApiServer("https://kubernetes.default.svc")
    .setRetryCount(3)
    .setRetryWait(2);

Client client = new Client(config);
```

## 3. 基本使用示例

### 查询Pod成本 (Cost API)

#### Go

```go
response, err := client.Cost.GetCost(context.Background(), &cost.CostRequest{
    DimensionType: "Pod",
    Dimension:     "pod=nginx-deployment-basic-75d6678cbb-lg8v5",
    TimeUnit:      "day",
})
if err != nil {
    log.Fatal(err)
}

for _, data := range *response {
    fmt.Printf("Pod: %s, Cost: %.2f\n", data.Metadata.PodName, data.Cost)
}
```

### 查询实时估算成本 (Cost V2 API)

#### Python

```python
response = client.cost_v2.get_cost_v2(
    window="yesterday",
    filter='namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
)

for data in response.data:
    for name, cost in data.items():
        print(f"Name: {name}, Cost: {cost.cost}")
```

### 查询业务分摊账单 (Allocation API)

#### JavaScript

```javascript
try {
    const response = await client.allocation.getAllocation({
        window: "yesterday",
        filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
    });

    response.data.forEach(data => {
        Object.entries(data).forEach(([name, allocation]) => {
            console.log(`Name: ${name}, Cost: ${allocation.cost}`);
        });
    });
} catch (error) {
    console.error('Error:', error);
}
```

### Java

```java
try {
    AllocationRequest request = new AllocationRequest()
        .setWindow("yesterday")
        .setFilter("namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");
    
    AllocationResponse response = client.getAllocation().getAllocation(request);
    
    for (Map<String, AllocationData> data : response.getData()) {
        for (Map.Entry<String, AllocationData> entry : data.entrySet()) {
            String name = entry.getKey();
            AllocationData allocation = entry.getValue();
            System.out.println("Name: " + name + ", Cost: " + allocation.getCost());
        }
    }
} catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
}
```

## 4. 高级功能

### 启用重试机制

所有语言的SDK都支持自动重试机制，可以通过配置来启用：

```go
// Go示例
client, err := cost.NewClient(&cost.Config{
    RetryCount: 5,  // 重试5次
    RetryWait:  3,  // 每次重试间隔3秒
})
```

### 处理错误

SDK提供了详细的错误处理机制：

#### Go

```go
response, err := client.CostV2.GetCostV2(context.Background(), &cost.CostV2Request{
    Window: "invalid-window",
})
if err != nil {
    if costErr, ok := err.(*cost.Error); ok {
        switch costErr.Code {
        case cost.ErrCodeBadRequest:
            fmt.Printf("Bad request: %s\n", costErr.Message)
        case cost.ErrCodeUnauthorized:
            fmt.Printf("Unauthorized: %s\n", costErr.Message)
        default:
            fmt.Printf("Error: %s\n", costErr.Message)
        }
    }
    return
}
```

## 5. 更多示例

请查看各语言SDK目录下的examples目录获取更多使用示例：

- [Go 示例](./go/examples/)
- [Python 示例](./python/examples/)
- [JavaScript 示例](./javascript/examples/)
- [Java 示例](./java/examples/)

## 6. 故障排除

### 常见问题

1. **认证问题**：确保您的Kubernetes集群凭证配置正确
2. **权限问题**：确保您的用户或服务账户具有访问API的RBAC权限
3. **网络问题**：确保可以访问Kubernetes API Server

### 获取帮助

如果您遇到问题，请：

1. 检查错误信息和日志
2. 参考API文档
3. 查看示例代码
4. 提交GitHub issue