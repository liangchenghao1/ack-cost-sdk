# Go SDK for ACK Cost API

阿里云ACK Cost API的Go语言SDK实现。

## 安装

```bash
go get github.com/AliyunContainerService/cost-sdk
```

## 快速开始

```go
package main

import (
    "context"
    "fmt"
    "log"
    openapi "github.com/AliyunContainerService/cost-sdk"
)

func main() {
    // 创建客户端
    proxyPath := "/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy"
    client, err := openapi.NewAPIClientWithKubeConfigAndProxy("", proxyPath)
    if err != nil {
        log.Fatalf("Failed to create client: %v", err)
    }

    // 查询成本数据
    ctx := context.Background()
    result, _, err := client.DefaultAPI.GetCost(ctx).
        Window("1h").
        Filter(`namespace:"kube-system"+controllerKind:"ReplicaSet"+label[app]:"ack-cost-exporter"`).
        Execute()

    if err != nil {
        log.Printf("API call failed: %v", err)
        return
    }

    // 显示结果
    if result != nil && result.Data != nil {
        fmt.Printf("Found %d time range data entries\n", len(result.Data))
        for i, timeRangeData := range result.Data {
            fmt.Printf("\nTime Range %d:\n", i+1)
            for podName, allocation := range timeRangeData {
                fmt.Printf("  Pod: %s\n", podName)
                if allocation.HasCost() {
                    fmt.Printf("    Cost: %.4f\n", allocation.GetCost())
                }
                if allocation.HasCpuCoreRequestAverage() {
                    fmt.Printf("    CPU: %.2f cores\n", allocation.GetCpuCoreRequestAverage())
                }
                if allocation.HasRamByteRequestAverage() {
                    fmt.Printf("    RAM: %.2f MB\n", allocation.GetRamByteRequestAverage()/1024/1024)
                }
            }
        }
    }
}
```

## API

| 方法 | 描述                             |
|------|--------------------------------|
| `GetCost` | 调用Cost API，用于查询实时估算成本          |
| `GetAllocation` | 调用Allocation API，用于查询业务分摊账单的成本 |

## 客户端创建

```go
// 使用默认 kubeconfig
client, err := openapi.NewAPIClientWithKubeConfigAndProxy("", proxyPath)

// 使用指定 kubeconfig
client, err := openapi.NewAPIClientWithKubeConfigAndProxy("/path/to/kubeconfig", proxyPath)

// 直接 API 访问
config := openapi.NewConfiguration()
config.Servers = openapi.ServerConfigurations{{URL: "https://your-api-server.com"}}
client := openapi.NewAPIClient(config)
```

## 示例

```bash
cd examples
go run cost_query_example.go
```