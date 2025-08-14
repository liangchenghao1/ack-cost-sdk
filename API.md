# ACK Cost API 多语言SDK文档

## 概述

ACK Cost API 提供了多种编程语言的SDK实现，以方便开发者在不同技术栈中集成成本查询功能。目前支持的语言包括：

- Go (官方主要实现)
- Python
- JavaScript
- Java

所有SDK都遵循统一的设计原则和功能特性，提供一致的API接口和使用体验。

## 功能特性

所有语言的SDK都支持以下核心功能：

1. **完整的API支持**:
   - Cost API (旧版)
   - Cost V2 API (推荐)
   - Allocation API (业务分摊账单)

2. **请求处理**:
   - 同步调用
   - 自动重试机制(可配置)
   - 超时控制

3. **错误处理**:
   - 清晰的错误分类
   - 详细的错误码说明
   - 符合各语言习惯的异常处理方式

## 各语言SDK详情

### Go SDK

Go SDK是官方主要实现，位于 [go/](go/) 目录。

#### 安装

```bash
go get github.com/AliyunContainerService/cost-sdk/go
```

#### 使用示例

```go
import costsdk "github.com/AliyunContainerService/cost-sdk/go"

// 创建客户端
client, err := costsdk.NewClient(&costsdk.Config{
    APIServer: "https://kubernetes.default.svc",
})
if err != nil {
    panic(err)
}

// 查询成本数据
request := &costsdk.CostV2Request{
    Window: "yesterday",
    Filter: `namespace:"kube-system"+controllerKind:"DaemonSet"`,
}

ctx := context.Background()
response, err := client.CostV2.GetCostV2(ctx, request)
if err != nil {
    // 处理错误
    return err
}

// 处理响应数据
for _, data := range response.Data {
    for name, cost := range data {
        fmt.Printf("Name: %s, Cost: %.3f\n", name, cost.Cost)
    }
}
```

### Python SDK

Python SDK位于 [python/](python/) 目录。

#### 安装

```bash
pip install -r requirements.txt
```

#### 使用示例

```python
from ack_cost_sdk import Client, CostV2Request

# 创建客户端
client = Client(
    api_server="https://kubernetes.default.svc",
    retry_count=3,
    retry_wait=2
)

# 查询成本数据
request = CostV2Request(
    window="yesterday",
    filter='namespace:"kube-system"+controllerKind:"DaemonSet"'
)

try:
    response = client.cost_v2.get_cost_v2(request)
    
    for data in response.data:
        for name, cost in data.items():
            print(f"Name: {name}, Cost: {cost.cost:.3f}")
except Exception as e:
    print(f"Error: {e}")
```

### JavaScript SDK

JavaScript SDK位于 [javascript/](javascript/) 目录。

#### 安装

```bash
npm install ack-cost-sdk
```

或者使用 yarn：

```bash
yarn add ack-cost-sdk
```

#### 使用示例

```javascript
const { Client, Config, CostV2Request } = require('ack-cost-sdk');

// 创建客户端
const client = new Client(new Config({
    apiServer: "https://kubernetes.default.svc",
    retryCount: 3,
    retryWait: 2
}));

// 查询成本数据
const request = new CostV2Request({
    window: "yesterday",
    filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"'
});

async function example() {
    try {
        const response = await client.costV2.getCostV2(request);
        
        response.data.forEach(data => {
            Object.entries(data).forEach(([name, cost]) => {
                console.log(`Name: ${name}, Cost: ${cost.cost.toFixed(3)}`);
            });
        });
    } catch (error) {
        console.error('Error:', error.message);
    }
}
```

### Java SDK

Java SDK位于 [java/](java/) 目录。

#### 安装

Maven:

```xml
<dependency>
    <groupId>com.aliyun.container.service</groupId>
    <artifactId>ack-cost-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle:

```gradle
implementation 'com.aliyun.container.service:ack-cost-sdk:1.0.0'
```

#### 使用示例

```java
import com.aliyun.container.service.cost.sdk.Client;
import com.aliyun.container.service.cost.sdk.Config;
import com.aliyun.container.service.cost.sdk.model.*;

// 创建客户端
Config config = new Config()
    .setApiServer("https://kubernetes.default.svc")
    .setRetryCount(3)
    .setRetryWait(2);

Client client = new Client(config);

// 查询成本数据
CostV2Request request = new CostV2Request()
    .setWindow("yesterday")
    .setFilter("namespace:\"kube-system\"+controllerKind:\"DaemonSet\"");

try {
    CostV2Response response = client.getCostV2().getCostV2(request);
    
    for (Map<String, CostData> data : response.getData()) {
        for (Map.Entry<String, CostData> entry : data.entrySet()) {
            String name = entry.getKey();
            CostData cost = entry.getValue();
            System.out.println("Name: " + name + ", Cost: " + cost.getCost());
        }
    }
} catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
}
```

## API参考

### Cost V2 API

Cost V2 API是推荐使用的成本查询接口，支持丰富的查询参数。

#### 请求参数

| 参数 | 类型 | 必需 | 描述 |
|------|------|------|------|
| window | string | 是 | 查询的时间窗口 |
| filter | string | 否 | 资源过滤条件 |
| step | string | 否 | 时间分段 |
| aggregate | string | 否 | 聚合维度 |
| idle | boolean | 否 | 是否展示闲置成本 |
| shareIdle | boolean | 否 | 是否分摊闲置成本 |
| shareSplit | string | 否 | 闲置分摊策略 |
| idleByNode | boolean | 否 | 是否按节点维度聚合闲置成本 |
| format | string | 否 | 成本导出格式 |

#### 响应数据结构

响应数据包含以下主要字段：

- name: 资源名称
- properties: 资源属性
- start: 开始时间
- end: 结束时间
- cpuCoreRequestAverage: 请求的平均CPU核心数
- cpuCoreUsageAverage: 使用的平均CPU核心数
- ramByteRequestAverage: 请求的平均内存量
- ramByteUsageAverage: 使用的平均内存量
- cost: 估算成本
- costRatio: 成本占比
- customCost: 自定义成本

### Allocation API

Allocation API用于查询业务分摊账单费用。

#### 请求参数

| 参数 | 类型 | 必需 | 描述 |
|------|------|------|------|
| window | string | 是 | 查询的时间窗口 |
| filter | string | 否 | 资源过滤条件 |
| step | string | 否 | 时间分段 |
| aggregate | string | 否 | 聚合维度 |
| idle | boolean | 否 | 是否展示闲置成本 |
| shareIdle | boolean | 否 | 是否分摊闲置成本 |
| shareSplit | string | 否 | 闲置分摊策略 |
| idleByNode | boolean | 否 | 是否按节点维度聚合闲置成本 |
| targetType | string | 否 | 成本分摊的目标类型 |
| format | string | 否 | 成本导出格式 |

## 错误处理

所有SDK都提供统一的错误处理机制，包含以下错误码：

- BadRequest (400): 请求参数错误
- Unauthorized (401): 未授权访问
- Forbidden (403): 禁止访问
- NotFound (404): 资源未找到
- TooManyRequests (429): 请求过于频繁
- InternalError (500): 内部服务器错误
- ServiceUnavailable (503): 服务不可用

## 最佳实践

1. **合理设置重试机制**:
   - 生产环境建议设置3-5次重试
   - 重试间隔建议设置为1-3秒

2. **正确处理错误**:
   - 根据错误类型采取不同的处理策略
   - 记录详细错误日志便于问题排查

3. **优化查询参数**:
   - 合理使用filter参数减少数据量
   - 根据需要选择合适的聚合维度

4. **资源管理**:
   - 及时关闭HTTP连接
   - 合理设置超时时间