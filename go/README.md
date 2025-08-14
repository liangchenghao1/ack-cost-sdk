# Go SDK for ACK Cost API

阿里云ACK成本API的Go语言SDK实现。

## 安装

```bash
go get github.com/AliyunContainerService/cost-sdk/go
```

## 功能特性

- 支持所有三种成本API:
  - Cost API (旧版)
  - Cost V2 API (推荐)
  - Allocation API (业务分摊账单)
- 同步和异步调用
- 自动重试机制
- 详细的错误处理
- 符合Go语言最佳实践

## 快速开始

### 创建客户端

```go
package main

import (
    "context"
    "fmt"
    "log"
    
    cost "github.com/AliyunContainerService/cost-sdk/go"
)

func main() {
    // 创建客户端
    client, err := cost.NewClient(&cost.Config{
        // API Server地址
        APIServer: "https://kubernetes.default.svc",
        // 可选: 重试配置
        RetryCount: 3,
        RetryWait:  2,
    })
    if err != nil {
        log.Fatal(err)
    }
    
    // 使用客户端...
}
```

### 调用Cost V2 API

```go
// 查询DaemonSet昨天的成本
response, err := client.CostV2.GetCostV2(context.Background(), &cost.CostV2Request{
    Window: "yesterday",
    Filter: `namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"`,
})
if err != nil {
    log.Fatal(err)
}

for _, data := range response.Data {
    for name, cost := range data {
        fmt.Printf("Name: %s, Cost: %.3f\n", name, cost.Cost)
    }
}
```

### 调用Allocation API

```go
// 查询业务分摊账单
response, err := client.Allocation.GetAllocation(context.Background(), &cost.AllocationRequest{
    Window: "yesterday",
    Filter: `namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"`,
})
if err != nil {
    log.Fatal(err)
}

for _, data := range response.Data {
    for name, allocation := range data {
        fmt.Printf("Name: %s, Cost: %.3f\n", name, allocation.Cost)
    }
}
```

## API参考

详细API文档请参考[API文档](../API.md)

## 示例

更多示例请参考[examples](./examples/)目录:

- [Cost V2 API 示例](./examples/cost_v2_example.go) - 包含多个使用Cost V2 API的示例
- [Allocation API 示例](./examples/allocation_example.go) - 展示如何使用Allocation API